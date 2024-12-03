package com.taketoritei.order.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taketoritei.order.login.form.User;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // リクエストにセット
		User user = new User(null);
		user.setRoomNo(request.getParameter("room"));
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, request.getParameter("token"));
        setDetails(request, authRequest);

        // cookieに設定
		Cookie roomCookie = new Cookie("roomNo", user.getRoomNo());
		roomCookie.setMaxAge(8640000);
		roomCookie.setPath("/");
		response.addCookie(roomCookie);

		Cookie tokenCookie = new Cookie("token", request.getParameter("token"));
		tokenCookie.setMaxAge(8640000);
		tokenCookie.setPath("/");
		response.addCookie(tokenCookie);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
