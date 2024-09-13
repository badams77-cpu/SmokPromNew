package com.smokpromotion.SmokProm.config.common;


import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

// URC-2261 - set as configuration rather than Component

@Configuration
public class FilterConfig {

    @Autowired
    private ApplicationContext applicationContext;

    private Environment environment;
//    private AnalyticsSender sender;
//    private AnalyticsTokenService tokenService;

    @Autowired
    public FilterConfig(Environment env) {
        //, AnalyticsSender sender, AnalyticsTokenService tokenService)

        this.environment = env;
    }
//        this.sender = sender;
//        this.tokenService = tokenService;
        @Order(1)
        @Bean
        public FilterRegistrationBean newFilterRegistrationBean(CharacterEncodingFilter filter) {
            final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);

            filterRegistrationBean.setFilter(new RequestResponseLoggingFilter());
         filterRegistrationBean.setOrder(1);
         filterRegistrationBean.setEnabled(false);
         filter.setEncoding("ISO-8859-1");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }



//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
 //       TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
 //       tomcat.addAdditionalTomcatConnectors(createSslConnector());
 //       return tomcat;
 //   }


    @Bean
    public ViewResolver htmlViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(htmlTemplateResolver()));
        resolver.setContentType("text/html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(new String[]{"*.html"});
        return resolver;
    }

    @Bean
    public ViewResolver javascriptViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(javascriptTemplateResolver()));
        resolver.setContentType("application/javascript");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(new String[]{"*.js"});
        return resolver;
    }

    @Bean
    public ViewResolver plainViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(plainTemplateResolver()));
        resolver.setContentType("text/plain");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(new String[]{"*.txt"});
        return resolver;
    }

    private ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

//    Finally, we need to create three separate template resolvers:

    private ITemplateResolver htmlTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

    private ITemplateResolver javascriptTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/js/");
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.JAVASCRIPT);
        return resolver;
    }

    private ITemplateResolver plainTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/txt/");
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.TEXT);
        return resolver;
    }

}