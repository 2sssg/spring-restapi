package me.practice.spring_practice_rest_api.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.practice.spring_practice_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class EventController {

	private final EventRepository eventRepository;

	private final ModelMapper modelMapper;

	private final EventValidator eventValidator;

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		if (errors.hasErrors())
			return getErrorsResourceResponseEntity(errors);

		eventValidator.validate(eventDto, errors);

		if (errors.hasErrors())
			return getErrorsResourceResponseEntity(errors);

		Event event = modelMapper.map(eventDto, Event.class);
		event.update();
		Event newEvent = this.eventRepository.save(event);
		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
		URI createdUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(newEvent);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		eventResource.add(Link.of("/docs/index.html#resources-events-create","profile"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}

	private ResponseEntity<ErrorsResource> getErrorsResourceResponseEntity(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
