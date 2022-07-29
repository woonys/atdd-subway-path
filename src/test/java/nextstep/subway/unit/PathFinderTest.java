package nextstep.subway.unit;

import static nextstep.subway.unit.LineStaticValues.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class PathFinderTest {
	Station 교대역;
	Station 강남역;
	Station 양재역;
	Station 남부터미널역;
	List<Section> sectionList;

	@BeforeEach
	void setUp() {
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");

		Line 이호선 = new Line("2호선", "green");
		Line 삼호선 = new Line("3호선", "orange");
		Line 신분당선 = new Line("신분당선", "red");

		이호선.addSection(교대역, 강남역, DISTANCE_VALUE_10);
		삼호선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_1);
		신분당선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_10);
		삼호선.addSection(남부터미널역, 양재역, DISTANCE_VALUE_3);

		List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
		sectionList = lines.stream()
			.flatMap(line -> line.getSections().stream())
			.collect(Collectors.toList());
	}

	@Test
	@DisplayName("최단경로 조회")
	void getShortestPaths() {

		/**
		 * 교대역    --- *2호선* ---   강남역
		 * |                        |
		 * *3호선*                   *신분당선*
		 * |                        |
		 * 남부터미널역  --- *3호선* ---   양재
		 */
		//when
		PathFinder pathFinder = new PathFinder(sectionList);
		List<Station> stationList = pathFinder.getShortestPath(교대역, 양재역);
		//then
		assertThat(stationList).hasSize(3)
			.containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(pathFinder.getSumOfDistance(교대역, 양재역))
			.isEqualTo(DISTANCE_VALUE_1 + DISTANCE_VALUE_3);
	}

}