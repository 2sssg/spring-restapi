package me.practice.spring_practice_rest_api.configs;

import java.util.Set;
import me.practice.spring_practice_rest_api.accounts.Account;
import me.practice.spring_practice_rest_api.accounts.AccountRole;
import me.practice.spring_practice_rest_api.accounts.AccountService;
import me.practice.spring_practice_rest_api.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {

			@Autowired AccountService accountService;

			@Autowired AppProperties appProperties;

			@Override
			public void run(ApplicationArguments args) throws Exception {

				Account admin = Account.builder()
						.email(appProperties.getAdminUsername())
						.password(appProperties.getPassword())
						.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
						.build();

				Account user = Account.builder()
						.email(appProperties.getUserUsername())
						.password(appProperties.getPassword())
						.roles(Set.of(AccountRole.USER))
						.build();

				accountService.saveAccount(admin);
				accountService.saveAccount(user);
			}
		};
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
