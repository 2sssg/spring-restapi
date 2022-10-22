package me.practice.spring_practice_rest_api.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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

	@Test
	void testFree() {

		// Given
		Event event = Event.builder()
				.basePrice(0)
				.maxPrice(0)
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isFree()).isTrue();
		// Given
		event = Event.builder()
				.basePrice(100)
				.maxPrice(0)
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isFree()).isFalse();

		// Given
		event = Event.builder()
				.basePrice(0)
				.maxPrice(100)
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isFree()).isFalse();
	}

	@Test
	void testOffline() {

		Event event = Event.builder()
				.location("화정동")
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isOffline()).isTrue();

		event = Event.builder()
				.build();
		// When
		event.update();
		// Then
		assertThat(event.isOffline()).isFalse();

	}
}