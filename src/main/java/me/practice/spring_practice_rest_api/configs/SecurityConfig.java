package me.practice.spring_practice_rest_api.configs;

import lombok.RequiredArgsConstructor;
import me.practice.spring_practice_rest_api.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired AccountService accountService;

	@Autowired PasswordEncoder passwordEncoder;

	@Bean
	TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http)
			throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(accountService)
				.passwordEncoder(passwordEncoder)
				.and()
				.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web -> web
				.ignoring()
				.antMatchers("/docs/index.html")
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
	}

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//		httpSecurity
//				.anonymous().and()
//				.formLogin().and()
//				.authorizeRequests().mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
//				.anyRequest().authenticated();
//
//
//		return httpSecurity.build();
//	}
}
