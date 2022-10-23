package me.practice.spring_practice_rest_api.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Arrays;
import me.practice.spring_practice_rest_api.events.Event;
import me.practice.spring_practice_rest_api.events.EventController;
import me.practice.spring_practice_rest_api.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

public class ErrorsResource  extends EntityModel<Errors> {
	public ErrorsResource(Errors errors, Link... links) {
		super(errors, Arrays.asList(links));
		add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
	}
}
