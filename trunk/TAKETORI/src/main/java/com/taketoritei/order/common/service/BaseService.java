package com.taketoritei.order.common.service;


import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected DSLContext jooq;

	protected String getLastUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    return auth.getName();
	}

	protected Timestamp getLastDate() {
		return new Timestamp(System.currentTimeMillis());
	}
}
