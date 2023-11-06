package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.util.CookieFactory;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Acceleration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableWebSecurity
@Profile(value = "portal")
public class PortalWebSecurityConfig implements WebSecurityConfigurer {

    // -----------------------------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------------------------

    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;
    private CsrfTokenRepository csrfTokenRepository;
    private MajoranaPayCustomAPISecurityFilter MajoranaPayCustomAPISecurityFilter;

    private MajoranaAccessDecisionManager maj;

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalWebSecurityConfig.class);

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public PortalWebSecurityConfig(PortalCustomAuthenticationProvider portalCustomAuthenticationProvider,
                                   CsrfTokenRepository csrfTokenRepository,
                                   @Value("${Majorana_COOKIE_DOMAIN:localhost}") String cookieDomain,
                                   MajoranaAccessDecisionManager maj

    ) {
        this.maj = maj;
        this.portalCustomAuthenticationProvider = portalCustomAuthenticationProvider;
        this.csrfTokenRepository = csrfTokenRepository;
        this.MajoranaPayCustomAPISecurityFilter = new MajoranaPayCustomAPISecurityFilter();
        CookieFactory.setCookieDomain(cookieDomain);
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


    @Override
    public void init(SecurityBuilder builder) throws Exception {

    }


//    public void configure(SecurityBuilder builder) throws Exception {

//    }


    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> {
                            try {
                                form
                                        .permitAll()
                                        .loginPage("/login")
                                        .failureHandler(getCustomAuthenticationFailureHandler())
                                        .usernameParameter("email")
                                        .passwordParameter("password")
                                        .defaultSuccessUrl("/login-handler")
                                        .and()
                                        .logout().logoutSuccessUrl("/")
                                        .and()
                                        .addFilterBefore(MajoranaPayCustomAPISecurityFilter,
                                                BasicAuthenticationFilter.class)


                                        .exceptionHandling().accessDeniedHandler(MajoranaAccessDeniedHandler());

                            } catch (Exception e) {
                                LOGGER.warn("Exception configuring form", e);
                            }
                        }
                )

        ;
        return null;
        // ...
    }


    @Override
    public void configure(SecurityBuilder securityBuilder) throws Exception {

        AccessDecisionVoter av = new AccessDecisionVoter() {
            @Override
            public boolean supports(ConfigAttribute attribute) {
                return true;
            }

            @Override
            public boolean supports(Class clazz) {
                return false;
            }

            @Override
            public int vote(Authentication authentication, Object object, Collection collection) {
                PortalSecurityPrinciple psp = (PortalSecurityPrinciple) authentication.getPrincipal();
                boolean acc = maj.checkAccessForPath(psp, authentication, );
                return 0;
            }
        };

        List<AccessDecisionVoter<?>> avl = new LinkedList<>();

        AccessDecisionManager acc = new UnanimousBased(avl);

        AuthorizationManager aam = RequestMatcherDelegatingAuthorizationManager.builder()
                .add(new AntPathRequestMatcher(""),
                        AuthorityAuthorizationManager.hasAuthority(PortalCustomAuthenticationProvider.ROLE_VAPI))
//                .add(new AntPathRequestMatcher("/resource/**"), AuthorityAuthorizationManager.hasAuthority("SCOPE_resource"))
                .build();

        avl.add(av);

                 AuthorizeHttpRequestsConfigurer http = (AuthorizeHttpRequestsConfigurer) (
                 (HttpSecurityBuilder) securityBuilder)
                 .getConfigurer(AuthorizeHttpRequestsConfigurer.class);

        HttpSecurityBuilder hsb = ((HttpSecurityBuilder<?>) securityBuilder);
             hsb.authenticationProvider(portalCustomAuthenticationProvider);

        HttpSecurity hs = (HttpSecurity) hsb.build();



//        AuthorityAuthorizationManager aam = AuthorityAuthorizationManager.hasAuthority();


//        AuthenticationManagerBuilder authManager =

     hs.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/token/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .httpBasic(Customizer.withDefaults())
             .authorizeHttpRequests(
                    auth->auth
     // items - should be a fairly restricted set of items
                   .requestMatchers("/","/css/**", "/images/**", "/public-js/**", "/login-handler","/login", "/client-login/**", "/exact-login","/favicon.ico", "/prec/**","/actuator/health" ).permitAll()
                   .requestMatchers(HttpMethod.POST, "/client-login-post").permitAll()

     //authenticated only - not role group specific
                .requestMatchers("/landing-page","/error", "/dashboard/ops/**", "/userpermission/api/**","/application-permission-admin/api/**","/dashboard/toReport/**", "/practices/lastupdate/**", "/development/**", "/dashboard/ceo/api/**", "/tips/api/**", "/notifications/**","/static/media/**",
                        "/practice-setup", "/settings/**", "/version-history", "/videos/**", "/accessDenied","/js-error", "/csrf-token", "/portal/api/userInformation", "/support","/your-account","/change-password/**", "/send-communication/**").authenticated()
                .requestMatchers("/generic/api/**").authenticated()
                .requestMatchers("/timeout").authenticated()
                .requestMatchers("/menu/api").authenticated()
                .requestMatchers("/js/adminportal/**").denyAll()
                .requestMatchers("/js/dentistportal/**").denyAll()
                .requestMatchers("/js/**").authenticated()
     // used for users-in-group, email job endpoints, kpi goal endpoints
                .requestMatchers(HttpMethod.GET, "/generic/api/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/generic/api/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/generic/api/**").authenticated()

                .requestMatchers(HttpMethod.PUT, "/generic/api/**").authenticated()


                            .anyRequest().access(aam)
     // authenticated and role group specific
//                .requestMatchers("/{basePath}/{path}/**")

                            //.access("@MajoranaAccessDecision.allowSection(request, authentication, #basePath, #path)")
//                .requestMatchers("/{basePath}/**")
                            //.access("@MajoranaAccessDecision.allowSection(request, authentication, #basePath)")
        //        .anyRequest().authenticated()

             );

      http.configure(hs);

    }

//    @Override
//    public void configure(WebSecurity web){
//            web.ignoring().requestMatchers("/UK","/EIRE","/NLD","/AUS","/NZ","/UAE", "/UK/login","/EIRE/login","/NLD/login","/AUS/login","/NZ/login","/UAE/login");
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


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(portalCustomAuthenticationProvider);
//    }

    @Bean
    public FilterRegistrationBean MajoranaPayfilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(MajoranaPayCustomAPISecurityFilter);
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

}