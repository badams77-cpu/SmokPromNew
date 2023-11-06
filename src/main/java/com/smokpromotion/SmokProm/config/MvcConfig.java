package com.smokpromotion.SmokProm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;
@EnableScheduling
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String DEFAULT_ANALYTICS_URL = "https://Majoranaadmin.imultipractice.com/analytics";
    private static final String LOCAL_ANALYTICS_URL = "http://localhost:9567/analytics";

    @Autowired
    private Environment env;

//    @Autowired
//    private DR_AnalyticsToken drAnalyticsToken;

    @Value("${Majorana_LOCAL_ANALYTICS:false}")
    private boolean localAnalytics;

 //   @Autowired
 //   private AnalyticsTokenService tokenService;

    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();

        source.setBasenames("i18n/message");
        source.setUseCodeAsDefaultMessage(true);

        // messageSource.setFallbackToSystemLocale(false);
        // messageSource.setCacheSeconds(0);
        // messageSource.setDefaultEncoding("UTF-8");

        return source;
    }


    @Bean(name = "localeResolver")
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        // See:
        // http://stackoverflow.com/questions/3191664/list-of-all-locales-and-their-short-codes
        // localeResolver.setDefaultLocale(Locale.UK);
        Locale defaultLocale = new Locale("en_GB"); // es_ES
        localeResolver.setDefaultLocale(defaultLocale);
        return localeResolver;
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

//    @Bean
//    public AnalyticsSender analyticsSender(){
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        String analyticsUrl = System.getenv("MajoranaADMIN_ANALYTICS_URL");
//        if (analyticsUrl==null){
//            analyticsUrl = DEFAULT_ANALYTICS_URL;
//        }
//        if (localAnalytics){
//            analyticsUrl = LOCAL_ANALYTICS_URL;
//        }
//        return new AnalyticsSender(restTemplate(), tokenService, analyticsUrl);
//    }




    // See: http://www.logicbig.com/tutorials/spring-framework/spring-core/formatter/
    // See: http://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.pdf (search for "implements Formatter<Date>")
    // See: https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-config-conversion
    // See: https://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html

    // @Override
    // public void addFormatters(FormatterRegistry registry) {
        // Add formatters and/or converters
    // }



}
