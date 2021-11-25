package nextstep.subway.line.domain;

import nextstep.subway.common.exception.SectionNotCreateException;
import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : Sections
 * author : haedoang
 * date : 2021/11/23
 * description : Sections 일급 컬렉션
 */
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addSection) {
        if (!sections.isEmpty()) {
            validate(addSection);
            updateSection(addSection);
        }
        sections.add(addSection);
    }

    private void updateSection(Section addSection) {
        if(getUpStations().contains(addSection.getStation())) {
            sections.stream()
                    .filter(section -> section.isSameStation(addSection))
                    .findFirst()
                    .ifPresent(section -> section.updateStation(addSection));
            return;
        }
        if(getDownStations().contains(addSection.getNextStation())) {
            sections.stream()
                    .filter(section -> section.isSameNextStation(addSection))
                    .findFirst()
                    .ifPresent(section -> section.updateNextStation(addSection));
        }
    }

    public List<Station> getStations() {
        return getOrderedStations(findFirstStation());
    }

    private void validate(Section section) {
        validateDistance(section);
        validateDuplicate(section);
        validateExist(section);
    }

    private void validateExist(Section section) {
        if (notExistStation(section)) {
            throw new SectionNotCreateException("구간에 역이 존재하지 않습니다.");
        }
    }

    private void validateDuplicate(Section section) {
        if (sections.stream().allMatch(it -> it.isDuplicate(section))) {
            throw new SectionNotCreateException("이미 등록된 구간입니다.");
        }
    }

    private void validateDistance(Section section) {
        findSection(section).ifPresent(it -> {
            if (!it.isPermitDistance(section.getDistance())) {
                throw new SectionNotCreateException("유효한 길이가 아닙니다.");
            }
        });
    }

    private List<Station> getOrderedStations(Station station) {
        List<Station> result = new ArrayList<>();
        while (Optional.ofNullable(station).isPresent()) {
            result.add(station);
            station = findNextStation(station);
        }
        return result;
    }

    private boolean notExistStation(Section section) {
        return !getStations().contains(section.getStation()) && !getStations().contains(section.getNextStation());
    }

    private Station findNextStation(Station station) {
        return sections.stream().filter(section -> section.getStation().equals(station))
                .findFirst()
                .orElse(new Section())
                .getNextStation();
    }

    private List<Station> getUpStations() {
        return sections.stream().map(Section::getStation).collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream().map(Section::getNextStation).collect(Collectors.toList());
    }

    private Optional<Section> findSection(Section findSection) {
        return sections.stream()
                .filter(section -> section.getStation().equals(findSection.getStation()))
                .findFirst();
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(section -> !getDownStations().contains(section.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getStation();
    }
}
