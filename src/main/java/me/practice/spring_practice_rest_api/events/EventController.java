package me.practice.spring_practice_rest_api.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.practice.spring_practice_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@GetMapping
	public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler) {

		Page<Event> page = this.eventRepository.findAll(pageable);
		var pagedResources =
				assembler.toModel(page, EventResource::new);

		pagedResources
				.add(Link.of("/docs/index.html#resources-events-list","profile"));
		return ResponseEntity.ok(pagedResources);
	}

	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {

		Optional<Event> optionalEvent = this.eventRepository.findById(id);

		if (optionalEvent.isEmpty())
			return ResponseEntity.notFound().build();

		EventResource eventResource = new EventResource(optionalEvent.get());
		eventResource
				.add(Link.of("/docs/index.html#resources-events-get","profile"));
		return ResponseEntity.ok(eventResource);
	}

	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id,
			@RequestBody @Valid EventDto eventDto, Errors errors) {

		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		if (errors.hasErrors()) {
			return getErrorsResourceResponseEntity(errors);
		}

		this.eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return getErrorsResourceResponseEntity(errors);
		}

		Event existingEvent = optionalEvent.get();
		this.modelMapper.map(eventDto, existingEvent);
		Event savedEvent = this.eventRepository.save(existingEvent);

		EventResource eventResource = new EventResource(savedEvent);
		eventResource
				.add(Link.of("/docs/index.html#resources-events-update","profile"));

		return ResponseEntity.ok(eventResource);
	}

	private ResponseEntity getErrorsResourceResponseEntity(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
