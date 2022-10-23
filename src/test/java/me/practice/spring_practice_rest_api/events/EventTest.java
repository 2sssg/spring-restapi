package me.practice.spring_practice_rest_api.events;

import static org.assertj.core.api.Assertions.assertThat;

import junitparams.JUnitParamsRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
class EventTest {

	@Test
	void builder() {
		Event event = Event.builder()
				.name("Inflearn Spring REST API")
				.description("REST API development with Spring")
				.build();
		assertThat(event).isNotNull();
	}

	@Test
	void javaBean() {
		// Given
		Event event = new Event();
		String name = "Event";
		String description = "Spring";

		// When
		event.setName(name);
		event.setDescription(description);

		// Then
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
	}

	private static Object[] parametersForTestFree() {
		return new Object[] {
				new Object[] {0, 0, true},
				new Object[] {100, 0, false},
				new Object[] {0, 100, false},
				new Object[] {100, 200, false}
		};
	}

	@ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
	@MethodSource("parametersForTestFree")
	void testFree(int basePrice, int maxPrice, boolean isFree) {

		// Given
		Event event = Event.builder()
				.basePrice(basePrice)
				.maxPrice(maxPrice)
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isFree()).isEqualTo(isFree);
	}


	private static Object[] parametersForTestOffline() {
		return new Object[] {
				new Object[] {"화정동", true},
				new Object[] {"", false},
				new Object[] {null, false},
				new Object[] {"           		", false}
		};
	}

	@ParameterizedTest(name = "{index} : location={0}, isOffline={1}")
	@MethodSource("parametersForTestOffline")
	void testOffline(String location, boolean isOffline) {

		Event event = Event.builder()
				.location(location)
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isOffline()).isEqualTo(isOffline);

	}
}