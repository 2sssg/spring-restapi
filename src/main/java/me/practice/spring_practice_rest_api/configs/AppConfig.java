package me.practice.spring_practice_rest_api.configs;

import java.util.Set;
import me.practice.spring_practice_rest_api.accounts.Account;
import me.practice.spring_practice_rest_api.accounts.AccountRole;
import me.practice.spring_practice_rest_api.accounts.AccountService;
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

			@Override
			public void run(ApplicationArguments args) throws Exception {
				Account account = Account.builder()
						.email("temp@temp.temp")
						.password("123")
						.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
						.build();

				accountService.saveAccount(account);
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
