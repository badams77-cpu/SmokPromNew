package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.util.CookieFactory;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Profile(value = "smok_app")
public class PortalWebSecurityConfig implements WebSecurityConfigurer<SecurityBuilder<jakarta.servlet.Filter>> {

    // -----------------------------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------------------------

    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;
    private CsrfTokenRepository csrfTokenRepository;
    private MajoranaCustomAPISecurityFilter majoranaCustomAPISecurityFilter;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public PortalWebSecurityConfig(PortalCustomAuthenticationProvider portalCustomAuthenticationProvider, CsrfTokenRepository csrfTokenRepository,  @Value("${Majorana_COOKIE_DOMAIN:localhost}") String cookieDomain) {

        this.portalCustomAuthenticationProvider = portalCustomAuthenticationProvider;
        this.csrfTokenRepository = csrfTokenRepository;
        this.majoranaCustomAPISecurityFilter = new MajoranaCustomAPISecurityFilter();
        CookieFactory.setCookieDomain(cookieDomain);
    }

    @Override
    public void init(SecurityBuilder auth) throws Exception {
        AuthenticationManagerBuilder b = (AuthenticationManagerBuilder) auth;
        b.authenticationProvider(portalCustomAuthenticationProvider);
    }

    //SecurityBuilder
    @Override
    public void configure(SecurityBuilder auth) throws Exception {
        AuthenticationManagerBuilder b = (AuthenticationManagerBuilder) auth;
        b.authenticationProvider(portalCustomAuthenticationProvider);
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(majoranaCustomAPISecurityFilter);
        registration.addUrlPatterns("/url/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("MajoranaCustomAPISecurityFilter");
        registration.setOrder(1);
        return registration;
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Protected Methods - WebSecurityConfigurerAdapter Overrides
    // -----------------------------------------------------------------------------------------------------------------

    // "select email, password, enabled from user where email=?"
    // "select u.email, ur.role from user_roles ur, user u where ur.user_id = u.id and u.email=?"

    // -----------------------------------------------------------------------------------------------------------------
    // Protected Methods - WebSecurityConfigurerAdapter Overrides
    // -----------------------------------------------------------------------------------------------------------------

    // "select email, password, enabled from user where email=?"
    // "select u.email, ur.role from user_roles ur, user u where ur.user_id = u.id and u.email=?"



//    @Override
    //public void configure(SecurityBuilder securityBuilder) throws Exception {

   //             HttpBasicConfigurer http = (HttpBasicConfigurer) (
   //             (HttpSecurityBuilder) securityBuilder)
   //             .getConfigurer(HttpBasicConfigurer.class);

//        http.formLogin()
//                .permitAll()
//                .loginPage("/login")
//                .failureHandler(getCustomAuthenticationFailureHandler())
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .defaultSuccessUrl("/login-handler")
//
//               .and()
//                .addFilterBefore(MajoranaPayCustomAPISecurityFilter, BasicAuthenticationFilter.class)
               // http //..csrfTokenRepository(csrfTokenRepository)
               // .and()
               // .authorizeRequests();
                // permitAll items - should be a fairly restricted set of items
 //               .antMatchers("/","/css/**", "/images/**", "/public-js/**", "/login-handler","/login", "/client-login/**", "/exact-login","/favicon.ico", "/prec/**","/actuator/health" ).permitAll()
 //               .antMatchers(HttpMethod.POST, "/client-login-post").permitAll()

                // authenticated only - not role group specific
//                .antMatchers("/landing-page","/error", "/dashboard/ops/**", "/userpermission/api/**","/application-permission-admin/api/**","/dashboard/toReport/**", "/practices/lastupdate/**", "/development/**", "/dashboard/ceo/api/**", "/tips/api/**", "/notifications/**","/static/media/**",
//                        "/practice-setup", "/settings/**", "/version-history", "/videos/**", "/accessDenied","/js-error", "/csrf-token", "/portal/api/userInformation", "/support","/your-account","/change-password/**", "/send-communication/**").authenticated()
//                .antMatchers("/generic/api/**").authenticated()
//                .antMatchers("/timeout").authenticated()
//                .antMatchers("/menu/api").authenticated()
//                .antMatchers("/js/adminportal/**").denyAll()
//                .antMatchers("/js/dentistportal/**").denyAll()
//                .antMatchers("/js/**").authenticated()
                // used for users-in-group, email job endpoints, kpi goal endpoints
//                .antMatchers(HttpMethod.GET, "/generic/api/**").authenticated()
//                .antMatchers(HttpMethod.PATCH, "/generic/api/**").authenticated()
//                .antMatchers(HttpMethod.POST, "/generic/api/**").authenticated()

//                .antMatchers(HttpMethod.PUT, "/generic/api/**").authenticated()

                // authenticated and role group specific
//                .antMatchers("/{basePath}/{path}/**").access("@MajoranaAccessDecision.allowSection(request, authentication, #basePath, #path)")
//                .antMatchers("/{basePath}/**").access("@MajoranaAccessDecision.allowSection(request, authentication, #basePath)")
//                .anyRequest().authenticated();

      //  http.logout().logoutSuccessUrl("/");

      //  http.exceptionHandling().accessDeniedHandler(MajoranaAccessDeniedHandler());
        

 //   }

//    @Override
//    public void configure(WebSecurity web){
//            web.ignoring().antMatchers("/UK","/EIRE","/NLD","/AUS","/NZ","/UAE", "/UK/login","/EIRE/login","/NLD/login","/AUS/login","/NZ/login","/UAE/login");
//    }

    @Bean
    @Profile("!test")
    public MajoranaAccessDecisionManager MajoranaAccessDecision() {

        return new MajoranaAccessDecisionManager();
    }

    @Bean
    public MajoranaAccessDeniedHandler MajoranaAccessDeniedHandler() {

        return new MajoranaAccessDeniedHandler();
    }

//    @Bean
//    public AuthenticationSuccessEventListener requestAuthenticationSuccessEventListener(){
//        AuthenticationSuccessEventListener requestAuthenticationSuccessEventListener = new AuthenticationSuccessEventListener();
//        return requestAuthenticationSuccessEventListener;
//    }
//
//    @Bean
//    public AuthenticationFailureListener requestAuthenticationFailureListener(){
//        AuthenticationFailureListener requestAuthenticationFailureListener = new AuthenticationFailureListener();
//        return requestAuthenticationFailureListener;
//    }
//
    @Bean
    public MajoranaAuthenticationFailureHandler getCustomAuthenticationFailureHandler(){
        MajoranaAuthenticationFailureHandler customAuthenticationFailureHandler = new MajoranaAuthenticationFailureHandler();
        return customAuthenticationFailureHandler;
    }




    @Bean
    public FilterRegistrationBean majoranaPayfilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(majoranaCustomAPISecurityFilter);
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

}