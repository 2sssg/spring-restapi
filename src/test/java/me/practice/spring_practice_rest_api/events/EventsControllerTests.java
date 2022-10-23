package me.practice.spring_practice_rest_api.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class EventsControllerTests {

	@Autowired MockMvc mockMvc;

	@Autowired ObjectMapper objectMapper;

	@Test
	@DisplayName("정상적인 이벤트")
	void createEvent() throws Exception {

		// Given, When
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2022, 10, 19, 18, 10))
				.closeEnrollmentDateTime(LocalDateTime.of(2022, 10, 20, 18, 10))
				.beginEventDateTime(LocalDateTime.of(2022, 10, 21, 18, 10))
				.endEventDateTime(LocalDateTime.of(2022, 10, 22, 18, 10))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("화정동")
				.build();

		// When
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		// Then
		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("id").value(Matchers.not(100)))
				.andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
	}

	@Test
	@DisplayName("잘못된 파라미터가 들어왔을 때 테스트")
	void createEventBadRequest() throws Exception {

		// Given, When
		Event event = Event.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2022, 10, 19, 18, 10))
				.closeEnrollmentDateTime(LocalDateTime.of(2022, 10, 20, 18, 10))
				.beginEventDateTime(LocalDateTime.of(2022, 10, 21, 18, 10))
				.endEventDateTime(LocalDateTime.of(2022, 10, 22, 18, 10))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("화정동")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.ENDED)
				.id(100)
				.build();

		// When
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		// Then
		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(event))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	@DisplayName("들어와야할 input값이 없을 때")
	void createEventBadRequestEmptyInput() throws Exception {
		EventDto eventDto = EventDto.builder().build();

		this.mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				;
	}

	@Test
	@DisplayName("input값이 잘못됐을 때")
	void createEventBadRequestWrongInput() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2022, 10, 19, 18, 10))
				.closeEnrollmentDateTime(LocalDateTime.of(2022, 10, 20, 18, 10))
				.beginEventDateTime(LocalDateTime.of(2022, 10, 24, 18, 10))
				.endEventDateTime(LocalDateTime.of(2022, 10, 23, 18, 10))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("화정동")
				.build();

		this.mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
//				.andExpect(jsonPath("$[0].field").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists())
				.andExpect(jsonPath("$[0].rejectedValue").exists())
		;
	}
}
