package com.smokpromotion.SmokProm;

import com.smokpromotion.SmokProm.config.common.YamlDBConfig;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@ComponentScan("com.smokpromotion")
@SpringBootApplication(exclude = { CassandraDataAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { CassandraDataAutoConfiguration.class })
public class SmokApplication   extends SpringBootServletInitializer {
	private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SmokApplication.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
//		DelegatingFilterProxy filter = new DelegatingFilterProxy("springSecurityFilterChain");
//		filter.setServletContext(servletContext);
//		filter.setTargetBeanName("Tomcat");
//		filter.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");
//		servletContext.addFilter("corsFilter", CorsFilter.class).addMappingForUrlPatterns(null, false, "/*");
//		servletContext.addFilter("springSecurityFilterChain", filter).addMappingForUrlPatterns(null, true, "/*");
	}


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


}


