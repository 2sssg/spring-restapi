package me.practice.spring_practice_rest_api.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import jdk.jfr.ContentType;
import me.practice.spring_practice_rest_api.common.RestDocsConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
class EventsControllerTests {

	@Autowired MockMvc mockMvc;

	@Autowired ObjectMapper objectMapper;

	@Autowired EventRepository eventRepository;

	@Autowired ModelMapper modelMapper;

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
				.location("town")
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
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				.andDo(document(
						"create-event",
						links(
								linkWithRel("self").description("link to self"),
								linkWithRel("query-events").description("link to query events"),
								linkWithRel("update-event").description("link to update an existing event"),
								linkWithRel("profile").description("link to profile")
						),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accpet header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
								fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
								fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("basePrice of new event"),
								fieldWithPath("maxPrice").description("maxPrice of new event"),
								fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("Location header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						relaxedResponseFields(
								fieldWithPath("id").description("identifier of new event"),
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
								fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
								fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("basePrice of new event"),
								fieldWithPath("maxPrice").description("maxPrice of new event"),
								fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
								fieldWithPath("free").description("it tells is this event is free or not"),
								fieldWithPath("offline").description("it tells is this event is offline or not"),
								fieldWithPath("eventStatus").description("event Status")
						)
				))
		;
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
				.andExpect(jsonPath("errors[0].objectName").exists())
//				.andExpect(jsonPath("$[0].field").exists())
				.andExpect(jsonPath("errors[0].defaultMessage").exists())
				.andExpect(jsonPath("errors[0].code").exists())
				.andExpect(jsonPath("errors[0].rejectedValue").exists())
		;
	}

	@Test
	@DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {

		// Given
		IntStream.range(0, 30).forEach(this::generateEvent);

		// When
		this.mockMvc.perform(get("/api/events")
						.param("page", "1")
						.param("size", "10")
						.param("sort", "name,DESC")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("query-events"))

		;
	}



	@Test
	@DisplayName("기존의 이벤트를 하나 조회하기")
	public void getEvent() throws Exception {

		//Given
		Event event = this.generateEvent(100);

		// When & Then
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())

		;
	}

	@Test
	@DisplayName("없는 이벤트를 조회했을 때 404 응답 받기")
	public void getEvent404() throws Exception {

		this.mockMvc.perform(get("/api/events/1213"))
				.andExpect(status().isNotFound())

		;
	}

	@Test
	@DisplayName("이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception {

		// Given
		Event event = this.generateEvent(200);

		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String updatedName = "Updated Event" + eventDto.getName().split(" ")[1];
		eventDto.setName(updatedName);


		// When && Then
		this.mockMvc.perform(
				put("/api/events/{id}", event.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventDto.getName()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
		;
	}


	@Test
	@DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
	public void updateEvent400ForEmpty() throws Exception {
		// Given
		Event event = this.generateEvent(200);

		EventDto eventDto = new EventDto();


		// When && Then
		this.mockMvc.perform(
				put("/api/events/{id}", event.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}


	@Test
	@DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
	public void updateEvent400ForIllegal() throws Exception {
		// Given
		Event event = this.generateEvent(200);

		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String updatedName = "Updated Event" + eventDto.getName().split(" ")[1];
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);


		// When && Then
		this.mockMvc.perform(
						put("/api/events/{id}", event.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}


	@Test
	@DisplayName("존재하지 않는 이벤트 수정 실패")
	public void updateEvent404() throws Exception {

		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);

		// When && Then
		this.mockMvc.perform(
						put("/api/events/13312")
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isNotFound())
		;
	}


	private Event generateEvent(int index) {

		Event event = Event.builder()
				.name("event " + index)
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2022, 10, 19, 18, 10))
				.closeEnrollmentDateTime(LocalDateTime.of(2022, 10, 20, 18, 10))
				.beginEventDateTime(LocalDateTime.of(2022, 10, 21, 18, 10))
				.endEventDateTime(LocalDateTime.of(2022, 10, 22, 18, 10))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("town")
				.build();

		return this.eventRepository.save(event);

	}
}
