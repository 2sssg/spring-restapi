package me.practice.spring_practice_rest_api.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;

	private final PasswordEncoder passwordEncoder;

	public Account saveAccount(Account account) {
		account.setPassword(this.passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	}

	public void deleteAllData() {
		accountRepository.deleteAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		return new AccountAdapter(account);
	}

	private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
				.map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
				.collect(Collectors.toSet());
	}
}
