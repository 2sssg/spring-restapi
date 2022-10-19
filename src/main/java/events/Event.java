package events;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

	// 추가 필드
	private Integer id;
	private boolean offline;
	private boolean free;
	private EventStatus eventStatus = EventStatus.DRAFT;

	//추가 필드

	private String name;
	private String description;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime begineEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임
	private int basePrice; // (optional)
	private int limitOfEnrollment;

}
