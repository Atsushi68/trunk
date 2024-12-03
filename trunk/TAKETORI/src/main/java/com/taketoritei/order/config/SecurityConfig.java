package com.taketoritei.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.taketoritei.order.login.service.AdminUserService;

/**
 * 管理画面用SecurityConfig
 */
@EnableWebSecurity
@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AdminUserService adminUserService;

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 無視するディレクトリ・ファイル
		web.ignoring().antMatchers("/css/**", "/img/**", "/js/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// URLへのアクセス制御
		http
		.antMatcher("/admin/**")
		.authorizeRequests() //
			.antMatchers("/admin/**")
			.hasAnyRole("ADMIN", "VIEWER")
			.anyRequest()
			.authenticated()
		.and()
		.exceptionHandling()
		.accessDeniedPage("/admin/login")
		.and()
		.formLogin() //
			.loginPage("/admin/login") //
			.loginProcessingUrl("/admin/auth") //
			.failureUrl("/admin/login?error") //
			.successHandler(LOGIN_SUCCESS).usernameParameter("id").passwordParameter("pass") //
		.and() //
		.logout()
			.logoutUrl("/admin/logout")
			.logoutSuccessUrl("/admin/login")
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true)
			.permitAll()
		.and()
		.sessionManagement()
		.invalidSessionUrl("/admin/login");
	}

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(adminUserService).passwordEncoder(new BCryptPasswordEncoder());

	}

	/**
	 * ログイン成功時の動作を定義
	 */
	private static final AuthenticationSuccessHandler LOGIN_SUCCESS = (request, response, auth) -> {
		String successPage = "/order/admin/top";
		response.sendRedirect(successPage);
	};

	 @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
