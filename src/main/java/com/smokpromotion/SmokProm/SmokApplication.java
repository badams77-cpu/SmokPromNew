package com.smokpromotion.SmokProm;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.smokpromotion.SmokProm.config.common.YamlDBConfig;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
//@EnableConfigurationProperties(com.smokpromotion.SmokProm.config.common.YamlDBConfig.class)
public class SmokApplication extends SpringBootServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		DbBean dbBean = new DbBean();
		try {
			dbBean.connect();
		} catch (Exception e){
			LOGGER.warn("Error connecting to Database");
		}
	}

	private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SmokApplication.class);


	public static void main(String[] args) {
		try {
			SpringApplication.run(SmokApplication.class, args);
		} catch (Exception e){
			LOGGER.warn("Exception e in Starting Spring",e);
			Throwable cause = e.getCause();
			while(cause !=null){
				LOGGER.warn("Exception caused by Starting Spring",cause);
				cause = cause.getCause();
			}
		}
	}

	private static void initLog4J() {

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SmokApplication.class);
	}
}


