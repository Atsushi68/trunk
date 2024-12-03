package com.taketoritei.order.config;

import java.nio.charset.StandardCharsets;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

	@Bean
	public MessageSource messageSource() {
	    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	    messageSource.setBasenames("i18n/messages");
	    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
	    messageSource.setFallbackToSystemLocale(false);
	    return messageSource;
	}
}
