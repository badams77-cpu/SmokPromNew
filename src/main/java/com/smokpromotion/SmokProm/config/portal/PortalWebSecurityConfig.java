package com.smokpromotion.SmokProm.config.portal;

import com.amazonaws.HttpMethod;
import com.smokpromotion.SmokProm.util.CookieFactory;
import jakarta.servlet.http.HttpServletRequest;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;

//import reactor.publisher.Mono;

@Configuration
@EnableWebSecurity
@Profile(value = "smok_app")
public class PortalWebSecurityConfig implements WebSecurityConfigurer<SecurityBuilder<jakarta.servlet.Filter>> {

    // -----------------------------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAccessDecisionManager.class);
    @Autowired
    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;

    @Autowired
    private UsernameAuthProvider usernameAuthenticationProvider;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;
    @Autowired
    private MajoranaCustomAPISecurityFilter majoranaCustomAPISecurityFilter;

//    @Autowired
//    private MajoranaAuthenticationFailureHandler majoranaAuthenticationFailureHandler;

    private MajoranaAccessDecisionManager decisionManager;

    @Autowired
    private  AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver;


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
        if (auth instanceof AuthenticationManagerBuilder b) {
            b.authenticationProvider(portalCustomAuthenticationProvider);
        } else if (auth instanceof WebSecurity) {

        }
    }


    //SecurityBuilder
//    @Override
    public void configure(SecurityBuilder auth) throws Exception {
        if (auth instanceof AuthenticationManagerBuilder b) {
            b.authenticationProvider(portalCustomAuthenticationProvider);
        } else if (auth instanceof WebSecurity) {

        }
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/","/index", "/login","login","login-handler",
                                "/login-handler","/home").permitAll()
                        .requestMatchers("/","/index", "/login","login","login-handler",
                                "/login-handler","/home").csrf().d
                        .requestMatchers("/a/**").authenticated()
                )
                .formLogin((form) -> {
                            try {
                                form
                                        .loginPage("/login").permitAll()
                                        .loginProcessingUrl("/login-handler").permitAll()
                                        .passwordParameter("password")
                                        .usernameParameter("username")
                                        .successForwardUrl("/a/home")
                                        .failureForwardUrl("/login/error=no_auth")
                                        .disable().csrf()

                                ;
                            //        .successHandler(majoranaLoginSuccessHandler)
                 //               form.failureHandler(majoranaAuthenticationFailureHandler);
                            } catch (Exception e) {
                                LOGGER.warn("configure form ex", e);

                            }
                        }
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }
 /*
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http

            .authenticationProvider(portalCustomAuthenticationProvider)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    .requestMatchers( "/login", "/public/**").permitAll()
                    .requestMatchers("/api/**", "/events/**", "/competition/**").authenticated()
            );
    http.formLogin()  .loginPage("/login").permitAll()
            .loginProcessingUrl("/login-handler").permitAll();
    return http.httpBasic(Customizer.withDefaults()).build();

}
*/
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("username")
                .password("password")
                .roles("USER")
                .build();
        auth.authenticationProvider(portalCustomAuthenticationProvider);
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


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        DefaultSecurityFilterChain build = http.authorizeHttpRequests(request ->
//                        request.requestMatchers( a->a.getPathInfo().startsWith("/auth"))
//                        .authenticated())
 //               .httpBasic(Customizer.withDefaults()).build();
 //               http.authenticationProvider(portalCustomAuthenticationProvider);
 //       return build;
 //   }

    /*
    public Mono<AuthorizationDecision> checkAccess(Mono authentication, Object object) {
        Authentication auth = (Authentication) authentication.block();

        AuthorizationContext context = (AuthorizationContext) object;

        try {

            PortalSecurityPrinciple prin = (PortalSecurityPrinciple) auth.getPrincipal();
            return Mono.just(new AuthorizationDecision(decisionManager.checkAccessForPath(
                    context.getExchange().getRequest(),
                    prin, auth,
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







    /*
@Bean
SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                            ReactiveAuthenticationManager reactiveAuthenticationManager) {

    Customizer flc = new Customizer<ServerHttpSecurity.FormLoginSpec>() {
        @Override
        public void customize(ServerHttpSecurity.FormLoginSpec fls) {
        }

    };

    http.formLogin(flc)
            .authorizeExchange(
                    b ->
                            b.pathMatchers("/openapi/openapi.yml").permitAll()
                                    .anyExchange()
                                    .authenticated()
            ).and().access((auth1, context1) ->

                    {

                        Authentication auth = (Authentication) auth1.block();

                        AuthorizationContext context = (AuthorizationContext) context1;
                        return ram.check(Mono.just(auth), context);
                    }
            )
            .anyExchange().denyAll();
    return http.build();
}
      */


/*
    @Override
    public void configure(uSecurityBuilder securityBilder) throws Exception {

       //         HttpBasicConfigurer http = (HttpBasicConfigurer) (
       //         (HttpSecurityBuilder) securityBuilder)
       //         .getConfigurer(HttpBasicConfigurer.class);

        FormLoginConfigurer flc =         (FormLoginConfigurer) ((HttpSecurityBuilder) securityBuilder)
                .getConfigurer(FormLoginConfigurer.class);

        flc
                //.formLogin()

                .loginPage("/login")
                .failureHandler(getCustomAuthenticationFailureHandler())
//                .usernameParameter("email")
//                .passwordParameter("password")
                .defaultSuccessUrl("/login-handler")

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
//                .antMatchers("/{basePath}/**").access("@MajoranaAccessDecision.allowSection(request, authentication, #basePath)")
//                .anyRequest().authenticated();

//        http.logout().logoutSuccessUrl("/");

//        http.exceptionHandling().accessDeniedHandler(MajoranaAccessDeniedHandler());
        



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
    public MajoranaAuthenticationFailureHandler getCustomAuthenticationFailureHandler() {
        MajoranaAuthenticationFailureHandler customAuthenticationFailureHandler = new MajoranaAuthenticationFailureHandler();
        return customAuthenticationFailureHandler;
    }


//    @Bean
//    public FilterRegistrationBean majoranaPayfilterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(majoranaCustomAPISecurityFilter);
//        registrationBean.addUrlPatterns("*");/
//
//
//               registrationBean.setOrder(1); //set precedence

//        return registrationBean;
//    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

}