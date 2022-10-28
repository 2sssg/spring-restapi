package me.practice.spring_practice_rest_api.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.practice.spring_practice_rest_api.accounts.Account;
import me.practice.spring_practice_rest_api.accounts.AccountAdapter;
import me.practice.spring_practice_rest_api.accounts.CurrentUser;
import me.practice.spring_practice_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors,
			@CurrentUser Account currentUser) {

		if (errors.hasErrors())
			return getErrorsResourceResponseEntity(errors);

		eventValidator.validate(eventDto, errors);

		if (errors.hasErrors())
			return getErrorsResourceResponseEntity(errors);

		Event event = modelMapper.map(eventDto, Event.class);
		event.update();
		event.setManager(currentUser);
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
	public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler,
			@CurrentUser Account currentUser) {

		Page<Event> page = this.eventRepository.findAll(pageable);
		var pagedResources = assembler.toModel(page, EventResource::new);
		pagedResources.add(Link.of("/docs/index.html#resources-events-list","profile"));

		if (currentUser != null)
			pagedResources.add(linkTo(EventController.class).withRel("create-event"));

		return ResponseEntity.ok(pagedResources);
	}

	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {

		Optional<Event> optionalEvent = this.eventRepository.findById(id);

		if (optionalEvent.isEmpty())
			return ResponseEntity.notFound().build();

		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		eventResource
				.add(Link.of("/docs/index.html#resources-events-get","profile"));
		if (event.getManager().equals(currentUser)) {
			eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
		}
		return ResponseEntity.ok(eventResource);
	}

	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors,
			@CurrentUser Account currentUser) {

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

		Event existingevent = optionalEvent.get();
		if (!existingevent.getManager().equals(currentUser)) {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
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
