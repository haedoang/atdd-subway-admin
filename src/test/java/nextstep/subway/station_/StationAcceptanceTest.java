package nextstep.subway.station_;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName : nextstep.subway.station_
 * fileName : StationAcceptanceTest
 * author : haedoang
 * date : 2021/11/18
 * description :
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    @LocalServerPort //로컬 포트 번호를 가져온다
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("지하철 역 생성하기")
    @Test
    void create() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when 지하철 역 생성 요청을 하면
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/stations")
                .then().log().all().extract();
        // then 지하철 역 생성 됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void show() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when 지하철 역 생성 요청을 하면
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/stations")
                .then().log().all().extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all().extract();

        // then
        List<StationResponse> stations = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stations.size()).isEqualTo(1);
    }
}
