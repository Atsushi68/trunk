package com.taketoritei.order.login.form;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import com.taketoritei.order.jooq.tables.records.MUserRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * 認証用のorg.springframework.security.core.userdetails継承クラス
 */
@Getter
@Setter
public class User implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String userName;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	private String roomNo;
	private String token;
	private Timestamp formDt;
	private Timestamp toDt;


	public User(MUserRecord record) {
		super();

		if (record == null)
			return;

		this.roomNo = null;
		this.token = null;

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		this.authorities = authorities;
		this.enabled = true;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setToken(String token) {
		this.token = token;
	}

}