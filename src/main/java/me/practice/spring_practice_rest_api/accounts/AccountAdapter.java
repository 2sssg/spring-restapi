package me.practice.spring_practice_rest_api.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class AccountAdapter extends User {

	private Account account;

	public AccountAdapter(Account account) {
		super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
		this.account = account;
	}

	private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
				.map(r -> new SimpleGrantedAuthority("ROLE_" + roles))
				.collect(Collectors.toSet());
	}

	public AccountAdapter(String username, String password,
			Collection<? extends GrantedAuthority> authorities,
			Account account) {
		super(username, password, authorities);
		this.account = account;
	}

}
