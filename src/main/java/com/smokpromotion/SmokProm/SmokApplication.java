package com.smokpromotion.SmokProm;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.smokpromotion.SmokProm.config.common.YamlDBConfig;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
public class SmokApplication  {



	private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SmokApplication.class);


	public static void main(String[] args) {
		try {
			new SpringApplicationBuilder(SmokApplication.class).web(WebApplicationType.SERVLET).run( args);
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

	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SmokApplication.class);
	}


	public void onStartup(ServletContext servletContext) throws ServletException {

	}
}


