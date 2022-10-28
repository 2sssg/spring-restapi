package me.practice.spring_practice_rest_api.common;


import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {


	@NotEmpty
	private String password;

	@NotEmpty
	private String adminUsername;

	@NotEmpty
	private String userUsername;

	@NotEmpty
	private String clientId;

	@NotEmpty
	private String clientSecret;
}
