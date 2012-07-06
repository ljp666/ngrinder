package org.ngrinder.user.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecuredUser extends User implements UserDetails {

	private static final long serialVersionUID = 9160341654874660746L;
	private final String userInfoProviderClass;
	private String authProviderClass;

	public SecuredUser(User user, String userInfoProviderClass) {
		this.userInfoProviderClass = userInfoProviderClass;
		this.authProviderClass = StringUtils.defaultIfEmpty(user.getAuthProviderClass(), userInfoProviderClass);
		setUserName(user.getUserName());
		setUserId(user.getUserId());
		setRole(user.getRole());
		setPsw(user.getPsw());
		setEmail(user.getEmail());
		setUserLanguage(user.getUserLanguage());
		setTimeZone(user.getTimeZone());
		setEnabled(true);
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>(1);
		roles.add(new SimpleGrantedAuthority(super.getRole().getName()));
		return roles;
	}

	@Override
	public String getPassword() {
		return super.getPsw();
	}

	@Override
	public String getUsername() {
		return super.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getUserInfoProviderClass() {
		return userInfoProviderClass;
	}

	public String getAuthProviderClass() {
		return authProviderClass;
	}

	public User getUser() {
		User user = new User();
		user.setUserName(getUserName());
		user.setUserId(getUserId());
		user.setEmail(getEmail());
		user.setRole(getRole());
		return user;
	}
}