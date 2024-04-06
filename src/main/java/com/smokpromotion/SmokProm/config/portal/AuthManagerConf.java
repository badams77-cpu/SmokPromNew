package com.smokpromotion.SmokProm.config.portal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Lazy
@Component
public class AuthManagerConf {

//    @Autowired
//    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAccessDecisionManager.class);

    private static final String LOGGED_IN_HOME_PAGE = "/portal/private/home.html";

    @Autowired
    private MajoranaAccessDecisionManager decisionManager;



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

        @Autowired
    public AuthManagerConf(PortalWebSecurityConfig pwsc){
            this.pwsc = pwsc;
        }

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
/*
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



        ServerAuthenticationSuccessHandler ash = new RedirectServerAuthenticationSuccessHandler(LOGGED_IN_HOME_PAGE);

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
    };

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
*/

}
