package com.taketoritei.order.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({ "com.taketori.order.jooq.tables" })
@EnableTransactionManagement
public class DataSourceConfig {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() throws SQLException {

		// SQLiteDataSource dataSource = new SQLiteDataSource();
		// dataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));

		// TODO SQLite以外を使用する場合
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
		dataSource.setUser(environment.getRequiredProperty("spring.datasource.username"));
		dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));

		return dataSource;
	}

	@Bean
	public DefaultConfiguration configuration() throws SQLException {
		DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
		jooqConfiguration.set(connectionProvider());
		jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

		// jooqConfiguration.set(SQLDialect.SQLITE);
		// TODO SQLite以外を使用する場合
		jooqConfiguration.set(SQLDialect.POSTGRES);

		return jooqConfiguration;
	}

	@Bean
	public TransactionAwareDataSourceProxy transactionAwareDataSource() throws SQLException {
		return new TransactionAwareDataSourceProxy(dataSource());
	}

	@Bean
	public DataSourceTransactionManager transactionManager() throws SQLException {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public DataSourceConnectionProvider connectionProvider() throws SQLException {
		return new DataSourceConnectionProvider(transactionAwareDataSource());
	}

	@Bean
	public JOOQExecuteListener exceptionTransformer() {
		return new JOOQExecuteListener();
	}

	@Bean
	public DefaultDSLContext dsl() throws SQLException {
		return new DefaultDSLContext(configuration());
	}

}
