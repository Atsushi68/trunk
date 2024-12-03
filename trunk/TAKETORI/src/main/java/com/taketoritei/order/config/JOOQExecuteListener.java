package com.taketoritei.order.config;

import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * JOOQのフック
 */
public class JOOQExecuteListener extends DefaultExecuteListener {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	public void exception(ExecuteContext context) {
		SQLDialect dialect = context.configuration().dialect();
		SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
		context.exception(translator.translate("Access database using jOOQ", context.sql(), context.sqlException()));
	}
}
