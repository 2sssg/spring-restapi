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
import me.practice.spring_practice_rest_api.common.AppProperties;
import me.practice.spring_practice_rest_api.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServerConfigTest extends BaseControllerTest {

	@Autowired AccountService accountService;

	@Autowired AppProperties appProperties;

	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	void getAuthToken() throws Exception {

		// When
		this.mockMvc.perform(post("/oauth/token")
						.with(httpBasic(
								appProperties.getClientId(),
								appProperties.getClientSecret()))
						.param("username", appProperties.getUserUsername())
						.param("password", appProperties.getPassword())
						.param("grant_type", "password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token").exists())
		;
	}
}