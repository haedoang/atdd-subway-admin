package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.line.LineAcceptanceTest.responseLine;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URI = "lines";

    private Long 구간_테스트_노선_ID;
    private Long 변경_상행종점역_ID;
    private Long 최초_상행종점역_ID;
    private Long 최초_하행종점역_ID;
    private Long 변경_하행종점역_ID;
    private Long 사이_추가_역_ID;
    private static final int 거리_5 = 5;
    private static final int 거리_100 = 100;
    private static final int 거리_150 = 150;

    @BeforeEach
    void setUpLine() {
        변경_상행종점역_ID = 지하철_역_등록되어_있음(변경_상행종점역);
        최초_상행종점역_ID = 지하철_역_등록되어_있음(최초_상행종점역);
        최초_하행종점역_ID = 지하철_역_등록되어_있음(최초_하행종점역);
        변경_하행종점역_ID = 지하철_역_등록되어_있음(변경_하행종점역);
        사이_추가_역_ID = 지하철_역_등록되어_있음(사이_추가_역);

        final LineRequest 구간_테스트_노선 = LineRequest.of("구간_테스트_노선", "사일런트라이트색", 최초_상행종점역_ID, 최초_하행종점역_ID, 거리_100);
        구간_테스트_노선_ID = 지하철_노선_등록되어_있음(구간_테스트_노선);
    }


    @DisplayName("역 사이에 새로운 역을 등록한다(upStation이 존재할 경우)")
    @Test
    void createSection() {
        // given
        SectionRequest 상행종점역_사이_추가역_거리_5 = SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 상행종점역_사이_추가역_거리_5);

        // then
        지하철_노선에_구간_생성됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList(최초_상행종점역_ID, 사이_추가_역_ID, 최초_하행종점역_ID));

    }


    @DisplayName("역 사이에 새로운 역을 등록한다(upStation이 존재하지 않을 경우)")
    @Test
    void createSection1() {
        // given
        SectionRequest 사이_추가역_하행종점역_거리_5 = SectionRequest.of(사이_추가_역_ID, 최초_하행종점역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 사이_추가역_하행종점역_거리_5);

        // then
        지하철_노선에_구간_생성됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList(최초_상행종점역_ID, 사이_추가_역_ID, 최초_하행종점역_ID));
    }

    @DisplayName("새로운 역을 상행 종점에 등록한다.")
    @Test
    void createSection2() {
        // given
        SectionRequest 변경_상행종점역_최초_상행종점역_거리_5 = SectionRequest.of(변경_상행종점역_ID, 최초_상행종점역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 변경_상행종점역_최초_상행종점역_거리_5);

        // then
        지하철_노선에_구간_생성됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList(변경_상행종점역_ID, 최초_상행종점역_ID, 최초_하행종점역_ID));
    }

    @DisplayName("새로운 역을 하행 종점에 등록한다.")
    @Test
    void createSection3() {
        // given
        SectionRequest 하행종점역_변경_하행종점역_거리_5 = SectionRequest.of(최초_하행종점역_ID, 변경_하행종점역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 하행종점역_변경_하행종점역_거리_5);

        // then
        지하철_노선에_구간_생성됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList(최초_상행종점역_ID, 최초_하행종점역_ID, 변경_하행종점역_ID));
    }

    @DisplayName("새로운 역을 등록할 경우 기존 구간 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void createSectionFail() {
        // given
        SectionRequest 기존_구간과_같은_거리인_경우 = SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_100);
        SectionRequest 기존_구간보다_길이가_큰_경우 = SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_150);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 기존_구간과_같은_거리인_경우);
        ExtractableResponse<Response> response2 = 구간_생성_요청함(구간_테스트_노선_ID, 기존_구간보다_길이가_큰_경우);

        // then
        구간_생성_실패됨_거리_예외(response);
        구간_생성_실패됨_거리_예외(response2);
    }


    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없어야 한다.")
    @Test
    void createSectionFail2() {
        // given
        SectionRequest 이미_등록된_구간 = SectionRequest.of(최초_상행종점역_ID, 최초_하행종점역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 이미_등록된_구간);

        // then
        구간_생성_실패됨_등록된_구간_예외(response);
    }


    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없어야 한다.")
    @Test
    void createSectionFail3() {
        // given
        SectionRequest 상행역_하행역_모두_포함_되어있지않음 = SectionRequest.of(변경_상행종점역_ID, 변경_하행종점역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = 구간_생성_요청함(구간_테스트_노선_ID, 상행역_하행역_모두_포함_되어있지않음);

        // then
        구간_생성_실패됨_역_없음_예외(response);
    }

    @DisplayName("사이의 역을 제거한다.")
    @Test
    void deleteInsideStation() {
        // given
        구간_생성_요청함(구간_테스트_노선_ID, SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청함(구간_테스트_노선_ID, 사이_추가_역_ID);

        // then
        노선에_구간_삭제됨(response);

        // when
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(구간_테스트_노선_ID);

        // then
        지하철_노선에_등록한_구간_포함됨(getResponse, Arrays.asList(최초_상행종점역_ID, 최초_하행종점역_ID));
    }

    @DisplayName("상행역을 제거한다.")
    @Test
    void deleteUpStation() {
        // given
        구간_생성_요청함(구간_테스트_노선_ID, SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청함(구간_테스트_노선_ID, 최초_상행종점역_ID);

        // then
        노선에_구간_삭제됨(response);

        // when
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(구간_테스트_노선_ID);

        // then
        지하철_노선에_등록한_구간_포함됨(getResponse, Arrays.asList(사이_추가_역_ID, 최초_하행종점역_ID));
    }

    @DisplayName("하행역을 제거한다.")
    @Test
    void deleteDownStation() {
        // given
        구간_생성_요청함(구간_테스트_노선_ID, SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청함(구간_테스트_노선_ID, 최초_하행종점역_ID);

        // then
        노선에_구간_삭제됨(response);

        // when
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(구간_테스트_노선_ID);

        // then
        지하철_노선에_등록한_구간_포함됨(getResponse, Arrays.asList(최초_상행종점역_ID, 사이_추가_역_ID));
    }

    @DisplayName("노선에 등록되어 있지 않은 역을 제거한다.")
    @Test
    void deleteNoDataStation() {
        // given
        구간_생성_요청함(구간_테스트_노선_ID, SectionRequest.of(최초_상행종점역_ID, 사이_추가_역_ID, 거리_5));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청함(구간_테스트_노선_ID, 변경_하행종점역_ID);

        // then
        구간_삭제_실패됨_없_없음_예외(response);
    }

    @DisplayName("구간이 하나인 경우 노선을 삭제한다")
    @Test
    void deleteWhenOnlyOneSection() {
        // when
        ExtractableResponse<Response> response = 구간_삭제_요청함(구간_테스트_노선_ID, 최초_상행종점역_ID);

        // then
        구간_삭제_실패됨_마지막_구간_예외(response);

        //when
        ExtractableResponse<Response> response2 = 구간_삭제_요청함(구간_테스트_노선_ID, 최초_하행종점역_ID);

        // then
        구간_삭제_실패됨_마지막_구간_예외(response2);
    }

    private ExtractableResponse<Response> 구간_생성_요청함(Long lineId, SectionRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(BASE_URI + "/{id}/sections", lineId)
                .then().log().all().extract();
    }

    private void 지하철_노선에_구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
    }

    private void 지하철_노선에_등록한_구간_포함됨(ExtractableResponse<Response> response, List<Long> expected) {
        LineResponse lineResponse = responseLine(response);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getId).containsExactlyElementsOf(expected);
    }

    private void 구간_생성_실패됨_등록된_구간_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("이미 등록된 구간입니다.");
    }

    private void 구간_생성_실패됨_역_없음_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("구간에 역이 존재하지 않습니다.");
    }

    private void 구간_생성_실패됨_거리_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("유효한 길이가 아닙니다.");
    }

    private void 구간_삭제_실패됨_없_없음_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("구간에 역이 존재하지 않습니다.");
    }

    private void 구간_삭제_실패됨_마지막_구간_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("구간을 더 이상 제거할 수 없습니다.");
    }

    private void 노선에_구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 구간_삭제_요청함(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(BASE_URI + "/{id}/sections", lineId)
                .then().log().all().extract();
    }

}
