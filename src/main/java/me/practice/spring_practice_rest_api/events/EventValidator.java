package me.practice.spring_practice_rest_api.events;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

	public void validate(EventDto eventDto, Errors errors) {

		if (eventDto.getBasePrice() > eventDto.getMaxPrice() &&
				eventDto.getMaxPrice() != 0) {
			errors.reject("wrongPrices", "Values to prices are Wrong");
		}

		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
				endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
				endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongValue",
					"endEventDateTime is wrong");
		}

		// TODO beginEventDateTime
		// TODO CloseEnrollmentDateTime
	}
}
