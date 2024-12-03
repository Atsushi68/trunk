package com.taketoritei.order.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.taketoritei.order.common.Consts.LangEnum;

@Configuration
@EnableWebSecurity
@Order(2)
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    CustomAuthenticationProvider authenticationProvider;

	@Autowired
	private Environment env;

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 無視するディレクトリ・ファイル
		web.ignoring().antMatchers("/css/**", "/img/**", "/js/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/user/login", "GET"));
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {

			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication auth) throws IOException, ServletException {
/*
//				// yamlから取得
				int cookieSaveTime = Integer.parseInt(env.getRequiredProperty("room.cookie-save-time"));
//
//				// ログイン情報取得
				User user = (User) auth.getPrincipal();

//				// cookieに設定
				Cookie roomCookie = new Cookie("roomNo", user.getRoomNo());
				roomCookie.setMaxAge(cookieSaveTime);
				roomCookie.setPath("/");
			    response.addCookie(roomCookie);
//
				Cookie tokenCookie = new Cookie("token", user.getToken());
				tokenCookie.setMaxAge(cookieSaveTime);
				tokenCookie.setPath("/");
			    response.addCookie(tokenCookie);
*/
				// TOPページにリダイレクト（java外）
				String successPage = env.getRequiredProperty("room.login-success-url");
				response.sendRedirect(successPage);

			}
		});
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

				System.out.println(request.getRequestURI());

				// TODO 自動生成されたメソッド・スタブ
				response.sendRedirect("/session/error/?lang=en");
			}
		});
        filter.setAuthenticationManager(authenticationManagerBean());

		// URLへのアクセス制御
        http.csrf().disable();
		http
			.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/session/error").permitAll()
				.antMatchers("/order/session/error").permitAll()
				.antMatchers("/user/login").permitAll()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/**").hasAnyRole("USER")
				.anyRequest()
				.authenticated()
			.and()
				.sessionManagement()
				.invalidSessionStrategy(new InvalidSessionStrategy() {

					// セッションが設定されていない場合の処理
					// ロケールの処理の前にエラーページに飛ばされるので、URLにロケールを設定する

					@Override
					public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

						String lang = "ja";
						String[] pathArray = request.getRequestURI().split("/", -1);

						if (pathArray.length >= 3) {
							String path2 = pathArray[2];

							if ("admin".equals(path2)) {
								// adminの場合はja

							} else {
								// adminでない場合は、LangEnumを含まれているかを確認する
								LangEnum[] langs = LangEnum.values();
								for (LangEnum l : langs)
									if (l.getCode().equals(path2))
										lang = path2;
							}
						}
						// TODO 自動生成されたメソッド・スタブ
						HttpSession session = request.getSession(true);
						session.setAttribute("lang", lang);
			            response.sendRedirect("/order/session/error");
					}
				})
			.and()
				.exceptionHandling()
				.accessDeniedPage("/session/error")
			.and()
			.addFilterBefore(filter, CustomAuthenticationFilter.class);
	}

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}
