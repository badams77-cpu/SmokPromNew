package com.smokpromotion.SmokProm.config.portal;

import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@Profile(value = "smok_app")
public class PortalMvcConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Override WebMvcConfigurerAdapter
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("portal/public/login");
        registry.addViewController("/error").setViewName("portal/error");
        registry.addViewController("/403").setViewName("portal/403");
    }
    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/");
    }

    // Uncomment code below to set allowed characters in path and query Strings
    /*
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return new MyCustomizer();
    }


    private static class MyCustomizer implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer factory) {
            if(factory instanceof TomcatEmbeddedServletContainerFactory) {
                customizeTomcat((TomcatEmbeddedServletContainerFactory) factory);
            }
        }

        void customizeTomcat(TomcatEmbeddedServletContainerFactory factory) {
            factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
                connector.setAttribute("relaxedPathChars", "<>[\\]£^`{|}");
                connector.setAttribute("relaxedQueryChars", "<>[\\]£^`{|}");
            });
        }

    }
    */


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

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        final HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        return repository;
    }

//    @Bean
//    LoginPageRedirectInterceptor loginInterceptor() {
//        return new LoginPageRedirectInterceptor( env.getActiveProfiles() );
//    }




    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
//        registry.addInterceptor(loginInterceptor());
    }

}
