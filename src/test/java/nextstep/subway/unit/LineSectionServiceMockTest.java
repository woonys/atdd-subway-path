package nextstep.subway.unit;

import nextstep.subway.applicaion.LineSectionService;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineSectionServiceMockTest {
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineSectionService lineSectionService;

    Line 신분당선;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        Section 논현_양재_구간 = new Section(신분당선, 논현역, 양재역, 10);
        신분당선.getSections().add(논현_양재_구간);
    }

    @Test
    @DisplayName("지하철역 추가 기능")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);

        when(stationService.findById(2L)).thenReturn(양재역);
        when(stationService.findById(3L)).thenReturn(양재시민의숲역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        // lineService.addSection 호출
        lineSectionService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getSections()).contains(양재_양재시민의숲_구간);
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하지 않는 경우")
    void addSectionException_withoutStations() {
        // given
        when(stationService.findById(stationIds.get(신사역))).thenReturn(신사역);
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(신사역), stationIds.get(강남역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하는 경우")
    void addSectionException_hasAllStations() {
        // given
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(논현역), stationIds.get(양재역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하지 않는 경우")
    void addSectionException_tooLongDistance() {
        // given
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(논현역), stationIds.get(강남역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }

    @Test
    @DisplayName("신규 상행 종점역으로 추가하는 경우")
    void addSection_WithNewLineUpStation() {
        // given
        when(stationService.findById(stationIds.get(신사역))).thenReturn(신사역);
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(lineService.findById(1L)).thenReturn(신분당선);
        Section 신사_논현_구간 = new Section(신분당선, 신사역, 논현역, 10);

        // when
        lineSectionService.addSection(1L, new SectionRequest(stationIds.get(신사역), stationIds.get(논현역), 10));

        // then
        assertThat(lineService.findById(1L).getSections().get(0)).isEqualTo(신사_논현_구간);
    }

    @Test
    @DisplayName("신규 하행 종점역으로 추가하는 경우")
    void addSection_WithNewLineDownStation() {
        // given
        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(stationService.findById(stationIds.get(양재시민의숲역))).thenReturn(양재시민의숲역);
        when(lineService.findById(1L)).thenReturn(신분당선);
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);

        // when
        lineSectionService.addSection(1L, new SectionRequest(stationIds.get(양재역), stationIds.get(양재시민의숲역), 10));

        // then
        assertThat(lineService.findById(1L).getSections().get(1)).isEqualTo(양재_양재시민의숲_구간);
    }

    @Test
    @DisplayName("기존 구간 사이에 상행역을 기준으로 추가하는 경우")
    void addSection_WithMiddleUpStation() {
        // given
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(lineService.findById(1L)).thenReturn(신분당선);
        Section 논현_강남_구간 = new Section(신분당선, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 6);

        // when
        lineSectionService.addSection(1L, new SectionRequest(stationIds.get(논현역), stationIds.get(강남역), 4));

        // then
        assertThat(lineService.findById(1L).getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

    @Test
    @DisplayName("기존 구간 사이에 하행역을 기준으로 추가하는 경우")
    void addSection_WithMiddleDownStation() {
        // given
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(lineService.findById(1L)).thenReturn(신분당선);
        Section 논현_강남_구간 = new Section(신분당선, 논현역, 강남역, 6);
        Section 강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 4);

        // when
        lineSectionService.addSection(1L, new SectionRequest(stationIds.get(강남역), stationIds.get(양재역), 4));

        // then
        assertThat(lineService.findById(1L).getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

    @ParameterizedTest
    @MethodSource("provideStations")
    @DisplayName("구간이 하나일 때 역을 제거하는 경우")
    void removeSection_leftOnlyOneSection(Station station) {
        // given
        when(stationService.findById(stationIds.get(station))).thenReturn(station);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        assertThatThrownBy(() -> {
            lineSectionService.deleteSection(1L, stationIds.get(station));
        }).isInstanceOf(SectionDeleteException.class)
                .hasMessage(ErrorType.CANNOT_REMOVE_LAST_SECTION.getMessage());
    }

    public static Stream<Arguments> provideStations() {
        return Stream.of(
                Arguments.of(논현역),
                Arguments.of(양재역)
        );
    }

    @Test
    @DisplayName("하행종점역 구간 삭제")
    void removeLastSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);
        신분당선.getSections().add(양재_양재시민의숲_구간);

        when(stationService.findById(stationIds.get(양재시민의숲역))).thenReturn(양재시민의숲역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        lineSectionService.deleteSection(1L, stationIds.get(양재시민의숲역));

        // then
        assertThat(lineService.findById(1L).getSections()).doesNotContain(양재_양재시민의숲_구간);
    }

    @Test
    @DisplayName("중간역 구간 삭제")
    void removeMiddleSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);
        신분당선.getSections().add(양재_양재시민의숲_구간);
        Section 논현_양재시민의숲_구간 = new Section(신분당선, 논현역, 양재시민의숲역, 20);

        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        lineSectionService.deleteSection(1L, stationIds.get(양재역));

        // then
        assertThat(lineService.findById(1L).getSections()).containsExactly(논현_양재시민의숲_구간);
    }

    @Test
    @DisplayName("상행종점역 구간 삭제")
    void removeFirstSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);
        신분당선.getSections().add(양재_양재시민의숲_구간);

        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        lineSectionService.deleteSection(1L, stationIds.get(논현역));

        // then
        assertThat(lineService.findById(1L).getSections()).containsExactly(양재_양재시민의숲_구간);
    }
}