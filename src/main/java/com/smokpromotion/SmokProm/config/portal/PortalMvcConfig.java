package com.smokpromotion.SmokProm.config.portal;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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
