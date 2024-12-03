package com.taketoritei.order.config;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.taketoritei.order.login.form.User;
import com.taketoritei.order.login.service.UserService;

@Configuration
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {


		User user = (User)auth.getPrincipal();

        // ここで認証とロールの付与
        Collection<GrantedAuthority> authorityList = new ArrayList<>();
        String roomNo = user.getRoomNo();
        String token = (String)auth.getCredentials();
        user.setToken(token);

        // トークンからユーザー情報を取得
        Record result = userService.getUserByLogin(roomNo, token);
        if (null == result) {
        	throw new BadCredentialsException("Authentication Error");
        } else {
        	// 通常ユーザーの設定
        	user.setFormDt((Timestamp)result.get("from_dt"));
        	user.setToDt((Timestamp)result.get("to_dt"));
        	authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return new UsernamePasswordAuthenticationToken(user, token, authorityList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
