package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.util.CookieFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import reactor.core.publisher.Mono;

import reactor.core.publisher.Mono;

import javax.naming.Context;

import java.io.IOException;
import java.net.URI;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.anyExchange;

//import reactor.publisher.Mono;

@Configuration
@EnableWebSecurity
@Profile(value = "smok_app")
public class PortalWebSecurityConfig implements WebSecurityConfigurer<SecurityBuilder<jakarta.servlet.Filter>> {

    // -----------------------------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAccessDecisionManager.class);

    private static final String LOGGED_IN_HOME_PAGE = "/portal/private/home.html";

    @Autowired
    private MajoranaAccessDecisionManager decisionManager;

    @Autowired
    private MajoranaCustomAPISecurityFilter majoranaCustomAPISecurityFilter;



    private ServerHttpSecurity serverHttpSecurity;

    private HttpSecurity httpSecurity;


    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public PortalWebSecurityConfig(
            //PortalCustomAuthenticationProvider portalCustomAuthenticationProvider,
            //                       CsrfTokenRepository csrfTokenRepository,
            //                       MajoranaAccessDecisionManager accessDecisionManager,
                                   @Value("${Majorana_COOKIE_DOMAIN:localhost}") String cookieDomain) {

    //    this.portalCustomAuthenticationProvider = portalCustomAuthenticationProvider;
    //    this.csrfTokenRepository = csrfTokenRepository;
    //    this.decisionManager = accessDecisionManager;
        this.majoranaCustomAPISecurityFilter = new MajoranaCustomAPISecurityFilter();
        CookieFactory.setCookieDomain(cookieDomain);
    }





    @Override
    public void init(SecurityBuilder auth) throws Exception {
        if (auth instanceof AuthenticationManagerBuilder) {
            AuthenticationManagerBuilder b = (AuthenticationManagerBuilder) auth;
            b.authenticationProvider(portalCustomAuthenticationProvider);
        } else if (auth instanceof WebSecurity) {

        }

    }

    //SecurityBuilder
    @Override

    public void configure(SecurityBuilder auth) throws Exception {
        if (auth instanceof AuthenticationManagerBuilder) {
            AuthenticationManagerBuilder b = (AuthenticationManagerBuilder) auth;
            b.authenticationProvider(portalCustomAuthenticationProvider);
        } else if (auth instanceof WebSecurity) {

        } else if (auth instanceof ServerHttpSecurity) {
            serverHttpSecurity = (ServerHttpSecurity) auth.build();
        } else if (auth instanceof HttpSecurity) {
            httpSecurity = (HttpSecurity) (auth.build());
        }
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

/*
    public Mono<AuthorizationDecision> checkAccess(Mono authentication, Object object) {
        Authentication auth = (Authentication) authentication.block();

        AuthorizationContext context = (AuthorizationContext) object;

        try {

            PortalSecurityPrinciple prin = (PortalSecurityPrinciple) auth.getPrincipal();
            return Mono.just(new AuthorizationDecision(decisionManager.checkAccessForPath(
                    context.getExchange().getRequest(),
                     auth,
                    context.getExchange().getRequest().getPath().contextPath().value())));
            //               hasRole("ADMIN").check(authentication, context)
            //           .switchIfEmpty(hasRole("DBA")
            //                                        .check(authentication, context))

        } catch (Exception e) {
            LOGGER.warn("IO exception ", e);
        }
        return Mono.just( new AuthorizationDecision(false));
    }
*/



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


    public PortalCustomAuthenticationProvider getPortalCustomAuthenticationProvider() {
        return portalCustomAuthenticationProvider;
    }

    public void setPortalCustomAuthenticationProvider(PortalCustomAuthenticationProvider portalCustomAuthenticationProvider) {
        this.portalCustomAuthenticationProvider = portalCustomAuthenticationProvider;
    }

    public CsrfTokenRepository getCsrfTokenRepository() {
        return csrfTokenRepository;
    }

    public void setCsrfTokenRepository(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    public MajoranaCustomAPISecurityFilter getMajoranaCustomAPISecurityFilter() {
        return majoranaCustomAPISecurityFilter;
    }

    public void setMajoranaCustomAPISecurityFilter(MajoranaCustomAPISecurityFilter majoranaCustomAPISecurityFilter) {
        this.majoranaCustomAPISecurityFilter = majoranaCustomAPISecurityFilter;
    }



    public ServerHttpSecurity getServerHttpSecurity() {
        return serverHttpSecurity;
    }

    public void setServerHttpSecurity(ServerHttpSecurity serverHttpSecurity) {
        this.serverHttpSecurity = serverHttpSecurity;
    }

    public HttpSecurity getHttpSecurity() {
        return httpSecurity;
    }

    public void setHttpSecurity(HttpSecurity httpSecurity) {
        this.httpSecurity = httpSecurity;
    }

    public ReactiveAuthenticationManager getReactiveAuthenticationManager() {
        return reactiveAuthenticationManager;
    }

    public void setReactiveAuthenticationManager(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
    }




/*
    @Override

 */

/*

    public void configure(SecurityBuilder securityBuilder) throws Exception {

        //         HttpBasicConfigurer http = (HttpBasicConfigurer) (
        //         (HttpSecurityBuilder) securityBuilder)
        //         .getConfigurer(HttpBasicConfigurer.class);

        FormLoginConfigurer flc = (FormLoginConfigurer) ((HttpSecurityBuilder) securityBuilder)
                .getConfigurer(FormLoginConfigurer.class);

        flc
                //.formLogin()

                .loginPage("/login")
                .failureHandler(getCustomAuthenticationFailureHandler())
//                .usernameParameter("email")
//                .passwordParameter("password")
                .defaultSuccessUrl("/login-handler");

//               http
//                .addFilterBefore(majoranaCustomAPISecurityFilter, BasicAuthenticationFilter.class)
//                .csrfTokenRepository(csrfTokenRepository)
//                .and()
//                                .authorizeRequests()
//                 permitAll items - should be a fairly restricted set of items
//                .antMatchers("/","/css/**", "/images/**", "/public-js/**", "/login-handler","/login", "/client-login/**", "/exact-login","/favicon.ico", "/prec/**","/actuator/health" )
//                .permitAll();
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
//                .antMatchers("/{basePath}/**").access("@MajoraProfnaAccessDecision.allowSection(request, authentication, #basePath)")
//                .anyRequest().authenticated();

//        http.logout().logoutSuccessUrl("/");

//        http.exceptionHandling().accessDeniedHandler(MajoranaAccessDeniedHandler());


    }

*/



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
        MajoranaAuthenticationFailureHandler customAuthenticationFailureHandler =
                new MajoranaAuthenticationFailureHandler();
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

    @Bean
    @Order(-500)
    SecurityWebFilterChain springWebFilterChain() {

        ServerHttpSecurity http = pwsc.getServerHttpSecurity();

        if (http==null){
            LOGGER.error("springWebFilterChain not set serverHttpSecurity is null");
        }

        Customizer flc = new Customizer<ServerHttpSecurity.FormLoginSpec>() {
            @Override
            public void customize(ServerHttpSecurity.FormLoginSpec fld) {
            }
        };

        ServerAuthenticationSuccessHandler ash = new ServerAuthenticationSuccessHandler(){
            @Override
            public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

                LOGGER.warn("Auth Success "+authentication.isAuthenticated()+" "+authentication.getName()
                        +" from "+ webFilterExchange.getExchange().getRequest().getRemoteAddress() );

                ServerHttpResponse swe = webFilterExchange.getExchange().getResponse();

                // Set HTTP status code to 302 (Found) for redirection
                swe.setStatusCode(HttpStatus.FOUND);
                swe.getHeaders().setLocation(URI.create(LOGGED_IN_HOME_PAGE));

                return swe.setComplete();

                //         webFilterExchange.getExchange().getResponse().writeAndFlushWith()
                //return null;.
            }

            //         public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            //             response.sendRedirect();
            //         }
            // LOGGED_IN_HOME_PAGE
        };

        ReactiveAuthorizationManager<AuthorizationContext> ram =  new ReactiveAuthorizationManager<AuthorizationContext>() {
            @Override
            public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
                return checkAccess( authentication, object);
            }
        };

    /*{
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.sendRedirect();
        }
    };*/

        //http // registerMatcher( a-> a
        http

                .authorizeExchange( b->b.anyExchange().access(ram));
        //   .and()
        //   .anyExchange().denyAll();;

        http.formLogin()
                .authenticationSuccessHandler( ash);

        //   http
        //.logoutSuccessHandler(lsh)

        return http.build();
    }

    private PortalWebSecurityConfig pwsc;



//    @Autowired
//    private HttpSecurity httpSecurity;

    @Lazy
    @Bean()
    @Order(-500)
    public ReactiveAuthenticationManager reactiveAuthManager() throws Exception {
        HttpSecurity http = pwsc.getHttpSecurity();

        ReactiveAuthenticationManager authenticationManager =
                http.getSharedObject(ReactiveAuthenticationManager.class);
        //     this.reactiveAuthenticationManager = authenticationManager;
        return authenticationManager;
    }

    @Lazy
    @Bean
    @Order(-500)
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(pwsc.getPortalCustomAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    public Mono<AuthorizationDecision> checkAccess(Mono authentication, Object object) {
        Authentication auth = (Authentication) authentication.block();

        AuthorizationContext context = (AuthorizationContext) object;

        try {

            PortalSecurityPrinciple prin = (PortalSecurityPrinciple) auth.getPrincipal();
            return Mono.just(new AuthorizationDecision(decisionManager.checkAccessForPath(
                    context.getExchange().getRequest(),
                    auth,
                    context.getExchange().getRequest().getPath().contextPath().value())));
            //               hasRole("ADMIN").check(authentication, context)
            //           .switchIfEmpty(hasRole("DBA")
            //                                        .check(authentication, context))

        } catch (Exception e) {
            LOGGER.warn("IO exception ", e);
        }
        return Mono.just( new AuthorizationDecision(false));
    }

}