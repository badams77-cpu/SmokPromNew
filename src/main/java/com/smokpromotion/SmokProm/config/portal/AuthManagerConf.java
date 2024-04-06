package com.smokpromotion.SmokProm.config.portal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class AuthManagerConf {

//    @Autowired
//    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;


        private PortalWebSecurityConfig pwsc;

        @Autowired
    public AuthManagerConf(PortalWebSecurityConfig pwsc){
            this.pwsc = pwsc;
        }

//    @Autowired
//    private HttpSecurity httpSecurity;


    @Bean()
//    @Order(100)
    public ReactiveAuthenticationManager authManager() throws Exception {
        HttpSecurity http = pwsc.getHttpSecurity();

        ReactiveAuthenticationManager authenticationManager =
                http.getSharedObject(ReactiveAuthenticationManager.class);
   //     this.reactiveAuthenticationManager = authenticationManager;
        return authenticationManager;
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(pwsc.getPortalCustomAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }



}
