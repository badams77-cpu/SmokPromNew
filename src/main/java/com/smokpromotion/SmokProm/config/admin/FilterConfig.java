package com.smokpromotion.SmokProm.config.admin;

//import com.urcompliant.analytics.AnalyticsSender;
//import com.urcompliant.analytics.AnalyticsTokenService;
import com.smokpromotion.SmokProm.config.portal.PortalMvcConfig;
import com.smokpromotion.SmokProm.config.portal.PortalWebSecurityConfig;
//import com.urcompliant.filter.AnalyticsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import jakarta.servlet.Filter;

// URC-2261 - set as configuration rather than Component
@Profile({"admin","dxpulse_admin"})
@Configuration
public class FilterConfig {

    public FilterConfig(){

    }

//    private Environment environment;
//    private AnalyticsSender sender;
//    private AnalyticsTokenService tokenService;
/*
    @Autowired
    public FilterConfig(Environment env, AnalyticsSender sender, AnalyticsTokenService tokenService){
        this.environment = env;
        this.sender = sender;
        this.tokenService = tokenService;
    }
*/
/*
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        AnalyticsFilter analyticsFilter = new AnalyticsFilter(sender, tokenService, environment);
//        registrationBean.setFilter(analyticsFilter);
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }
*/
}