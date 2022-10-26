package me.practice.spring_practice_rest_api.accounts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AccountServiceTest {

	@Autowired AccountService accountService;

	@Autowired PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("정상적인 유저 찾기")
	public void findByUsername() {

		// Given
		String password = "123";
		String username = "lsg020302@kau.kr";
		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();

		this.accountService.saveAccount(account);

		// When
		UserDetailsService userDetailsService = this.accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// Then
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
	}


	@Test
	@DisplayName("잘못된 email로 유저 찾기")
	void findByUsernameFail() {

		String username = "lsg020302@kau.kr";
//		 junit 5

		UsernameNotFoundException thrown = assertThrows(
				UsernameNotFoundException.class,
				() -> accountService.loadUserByUsername(username));

	}

}