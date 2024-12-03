package com.taketoritei.order.login.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.taketoritei.order.jooq.tables.records.MAdminRecord;

/**
 * 認証用のorg.springframework.security.core.userdetails継承クラス
 */
public class AdminUser implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String password;
	private String adminId;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private String adminName;

	public AdminUser(MAdminRecord record) {
		super();

		if (record == null)
			return;

		this.adminId = record.getAdminId();
		this.adminName = record.getAdminName();
		this.password = record.getPassword();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(record.getRole()));
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
		return this.adminName;
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

	public String getUserId() {
		return this.adminId;
	}

	public String getUserName() {
		return adminName;
	}

	public void setUserName(String adminName) {
		this.adminName = adminName;
	}

}