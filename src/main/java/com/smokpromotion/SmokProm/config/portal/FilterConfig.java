package com.smokpromotion.SmokProm.config.portal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Profile("smok_app")
@Configuration
public class FilterConfig {

    private Environment environment;

//    private AnalyticsSender sender;
//    private AnalyticsTokenService tokenService;

    @Autowired
    public FilterConfig(Environment env){ //), AnalyticsSender sender, AnalyticsTokenService tokenService){
        this.environment = env;
//        this.sender = sender;
//        this.tokenService = tokenService;
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        AnalyticsFilter analyticsFilter = new AnalyticsFilter(sender, tokenService, environment);
//        registrationBean.setFilter(analyticsFilter);
//        registrationBean.addUrlPatterns("*");
//        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }


}