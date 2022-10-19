package me.practice.spring_practice_rest_api.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class EventsControllerTests {

	@Autowired MockMvc mockMvc;

	@Autowired ObjectMapper objectMapper;

	@MockBean EventRepository eventRepository;

	@Test
	public void createEvent() throws Exception {

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
				.build();

		// When
		event.setId(1);
		Mockito.when(eventRepository.save(event)).thenReturn(event);

		// Then
		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(event))
				)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists());
	}
}
