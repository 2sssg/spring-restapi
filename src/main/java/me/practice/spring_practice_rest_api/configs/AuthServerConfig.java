package me.practice.spring_practice_rest_api.configs;

import lombok.RequiredArgsConstructor;
import me.practice.spring_practice_rest_api.accounts.AccountService;
import me.practice.spring_practice_rest_api.common.AppProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	private final AccountService accountService;

	private final TokenStore tokenStore;

	private final AppProperties appProperties;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient(appProperties.getClientId())
				.authorizedGrantTypes("password", "refresh_token")
				.scopes("read", "write")
				.secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
				.accessTokenValiditySeconds(10 * 60)
				.refreshTokenValiditySeconds(6 * 10 * 60);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
				.userDetailsService(accountService)
				.tokenStore(tokenStore);
	}
}
