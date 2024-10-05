package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.config.portal.UsernameAuthProvider;
import com.smokpromotion.SmokProm.util.RunEnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.http.HttpServletRequest;

@Configuration
@Profile(value = {"smok_admin"})
public class AdminMvcConfig implements WebMvcConfigurer {

    @Autowired
    Environment env;

    @Autowired
    private RunEnvironmentUtils runEnvironmentUtils;

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Override WebMvcConfigurerAdapter
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName(getLoginTemplate());
        registry.addViewController("/error").setViewName(getErrorTemplate());
        registry.addViewController("/403").setViewName(getAccessDeniedTemplate());
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        final HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }

    private String getLoginTemplate() {
        if (runEnvironmentUtils.isDentrix()) {
            return "dxpulse_admin/login";
        }

        return "admin/login";
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }


    @Bean
    public AuthenticationManagerResolver<HttpServletRequest>
    tokenAuthenticationManagerResolver(
            UsernameAuthProvider
                    //        AuthenticationProvider
                    authProvider
    ) {
        return (request)-> {
            return authProvider::authenticate;
        };
    }


    public void addInterceptors(InterceptorRegistry registry) {

//        registry.addInterceptor(loginInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
    }

    private String getErrorTemplate() {
        if (runEnvironmentUtils.isDentrix()) {
            return "dxpulse_admin/error";
        }

        return "admin/error";
    }

    private String getAccessDeniedTemplate() {
        if (runEnvironmentUtils.isDentrix()) {
            return "dxpulse_admin/private/accessDenied";
        }
        return "admin/private/accessDenied";
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPortalEnumConverter());
    }
}
