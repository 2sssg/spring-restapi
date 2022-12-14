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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import me.practice.spring_practice_rest_api.accounts.Account;
import me.practice.spring_practice_rest_api.accounts.AccountRole;
import me.practice.spring_practice_rest_api.accounts.AccountService;
import me.practice.spring_practice_rest_api.common.AppProperties;
import me.practice.spring_practice_rest_api.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

class EventsControllerTests extends BaseControllerTest {

	@Autowired EventRepository eventRepository;

	@Autowired AccountService accountService;

	@Autowired AppProperties appProperties;

	@BeforeEach
	public void setUp() {
		this.eventRepository.deleteAll();
		this.accountService.deleteAllData();
	}

	@Test
	@DisplayName("???????????? ?????????")
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
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
	@DisplayName("????????? ??????????????? ???????????? ??? ?????????")
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
				.location("?????????")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.ENDED)
				.id(100)
				.build();

		// When
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		// Then
		mockMvc.perform(post("/api/events/")
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(event))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	@DisplayName("??????????????? input?????? ?????? ???")
	void createEventBadRequestEmptyInput() throws Exception {
		EventDto eventDto = EventDto.builder().build();

		this.mockMvc.perform(post("/api/events")
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				;
	}

	@Test
	@DisplayName("input?????? ???????????? ???")
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
				.location("?????????")
				.build();

		this.mockMvc.perform(post("/api/events")
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
	@DisplayName("30?????? ???????????? 10?????? ????????? ????????? ????????????")
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
	@DisplayName("30?????? ???????????? 10?????? ????????? ????????? ????????????")
	public void queryEventsWithAuthentication() throws Exception {

		// Given
		IntStream.range(0, 30).forEach(this::generateEvent);

		// When
		this.mockMvc.perform(get("/api/events")
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
				.andExpect(jsonPath("_links.create-event").exists())
				.andDo(document("query-events"))

		;
	}



	@Test
	@DisplayName("????????? ???????????? ?????? ????????????")
	public void getEvent() throws Exception {

		//Given
		Account account = this.createAccount();
		Event event = this.generateEvent(100, account);

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
	@DisplayName("?????? ???????????? ???????????? ??? 404 ?????? ??????")
	public void getEvent404() throws Exception {

		this.mockMvc.perform(get("/api/events/1213"))
				.andExpect(status().isNotFound())

		;
	}

	@Test
	@DisplayName("???????????? ??????????????? ????????????")
	public void updateEvent() throws Exception {

		// Given
		Account account = this.createAccount();
		Event event = this.generateEvent(200, account);

		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String updatedName = "Updated Event" + eventDto.getName().split(" ")[1];
		eventDto.setName(updatedName);


		// When && Then
		this.mockMvc.perform(
				put("/api/events/{id}", event.getId())
						.header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
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
	@DisplayName("???????????? ???????????? ????????? ????????? ?????? ??????")
	public void updateEvent400ForEmpty() throws Exception {
		// Given
		Event event = this.generateEvent(200);

		EventDto eventDto = new EventDto();


		// When && Then
		this.mockMvc.perform(
				put("/api/events/{id}", event.getId())
						.header(HttpHeaders.AUTHORIZATION, getBearerToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}


	@Test
	@DisplayName("???????????? ????????? ????????? ????????? ?????? ??????")
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
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}


	@Test
	@DisplayName("???????????? ?????? ????????? ?????? ??????")
	public void updateEvent404() throws Exception {

		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);

		// When && Then
		this.mockMvc.perform(
						put("/api/events/13312")
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto))
				)
				.andDo(print())
				.andExpect(status().isNotFound())
		;
	}


	private Event generateEvent(int index) {

		Event oneEvent = createOneEvent();

		return generateEvent(oneEvent, index);
	}

	private Event generateEvent(Event event, int index) {

		event.setName("event " + index);

		return this.eventRepository.save(event);
	}

	private Event generateEvent(int index, Account account) {

		Event event = createOneEvent();
		event.setManager(account);

		return generateEvent(event, index);
	}

	private Event createOneEvent() {

		return Event.builder()
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
	}




	private String getAccessToken(boolean needToCreateAccount) throws Exception {

		// Given
		if (needToCreateAccount)
			createAccount();

		// When
		ResultActions perform = this.mockMvc.perform(post("/oauth/token")
				.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
				.param("username", appProperties.getUserUsername())
				.param("password", appProperties.getPassword())
				.param("grant_type", "password"));

		String response = perform.andReturn().getResponse().getContentAsString();
		Jackson2JsonParser parser = new Jackson2JsonParser();

		return parser.parseMap(response).get("access_token").toString();
	}

	private Account createAccount() {
		Account account = Account.builder()
				.email(appProperties.getUserUsername())
				.password(appProperties.getPassword())
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		return this.accountService.saveAccount(account);
	}


	private String getBearerToken() throws Exception {
		return getBearerToken(true);
	}

	private String getBearerToken(boolean needToCreateAccount) throws Exception {
		return "Bearer " + getAccessToken(needToCreateAccount);
	}

}
