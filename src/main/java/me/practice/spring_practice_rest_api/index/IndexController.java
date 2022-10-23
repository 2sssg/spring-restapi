package me.practice.spring_practice_rest_api.index;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import me.practice.spring_practice_rest_api.events.EventController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/api")
	public RepresentationModel index() {
		var index = new RepresentationModel();
		index.add(linkTo(EventController.class).withRel("events"));
		return index;
	}
}
