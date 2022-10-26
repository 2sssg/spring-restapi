package me.practice.spring_practice_rest_api.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import me.practice.spring_practice_rest_api.accounts.Account;
import me.practice.spring_practice_rest_api.accounts.AccountRole;
import me.practice.spring_practice_rest_api.accounts.AccountService;
import me.practice.spring_practice_rest_api.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServerConfigTest extends BaseControllerTest {

	@Autowired AccountService accountService;

	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	void getAuthToken() throws Exception {

		// Given
		String clientId = "myApp";
		String clientSecret = "pass";
		String email = "temp@temp.temp";
		String password = "123";
		Account account = Account.builder()
				.email(email)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		accountService.saveAccount(account);

		// When
		this.mockMvc.perform(post("/oauth/token")
						.with(httpBasic(clientId, clientSecret))
						.param("username", email)
						.param("password", password)
						.param("grant_type", "password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token").exists())
		;
	}
}