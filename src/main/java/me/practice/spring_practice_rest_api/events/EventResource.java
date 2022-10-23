package me.practice.spring_practice_rest_api.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

@Getter
@EqualsAndHashCode(callSuper = false)
public class EventResource extends EntityModel<Event> {

	@JsonUnwrapped
	private Event event;

	public EventResource(Event event, Link... links) {
		super(event, Arrays.asList(links));
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
}
