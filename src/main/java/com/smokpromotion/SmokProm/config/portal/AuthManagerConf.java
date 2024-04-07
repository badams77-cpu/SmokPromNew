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

    private PortalWebSecurityConfig  pwsc;

    @Autowired
    public AuthManagerConf(PortalWebSecurityConfig pwsc){
        this.pwsc = pwsc;
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
