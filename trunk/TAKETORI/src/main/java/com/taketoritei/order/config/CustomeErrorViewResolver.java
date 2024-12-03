package com.taketoritei.order.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomeErrorViewResolver extends DefaultErrorViewResolver {

	public CustomeErrorViewResolver(ApplicationContext applicationContext, ResourceProperties resourceProperties) {
		super(applicationContext, resourceProperties);
	}

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {

		final ModelAndView mav = super.resolveErrorView(request, status, model);

		if (status.value() == HttpStatus.FORBIDDEN.value()) {

			// ユーザー画面にはログイン画面が無い為403エラー時の処理をハンドリング
			// セッションが無い場合に直接URLを指定した場合403エラーになる
			// セッションエラー画面にリダイレクト
			ModelAndView modelAndView =  new ModelAndView("redirect:/session/error");
			return modelAndView;

		} else if (status.is4xxClientError()) {
			// 4XX系エラーの時の処理

		} else if (status.is5xxServerError()) {
			// 5XX系エラーの時の処理
		}
		return mav;
	}
}
