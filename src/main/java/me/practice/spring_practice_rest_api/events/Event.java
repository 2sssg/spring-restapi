package me.practice.spring_practice_rest_api.events;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.practice.spring_practice_rest_api.accounts.Account;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

	// 추가 필드
	@Id @GeneratedValue
	private Integer id;
	private boolean offline;
	private boolean free;
	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus = EventStatus.DRAFT;

	//추가 필드

	private String name;
	private String description;
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임
	private int basePrice; // (optional)
	private int maxPrice;
	private int limitOfEnrollment;
	@ManyToOne
	private Account manager;

	public void update() {
		updateFree();
		updateOffline();
	}

	private void updateFree() {
		this.free = this.basePrice == 0 && this.maxPrice == 0;
	}

	private void updateOffline() {
		this.offline = !(this.location == null || this.location.isBlank());
	}
}
