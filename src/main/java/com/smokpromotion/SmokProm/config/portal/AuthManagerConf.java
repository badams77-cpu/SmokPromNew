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

    @Autowired
    private PortalCustomAuthenticationProvider portalCustomAuthenticationProvider;

    @Autowired
    private HttpSecurity httpSecurity;



    @Bean
    public ReactiveAuthenticationManager authManager() throws Exception {
        ReactiveAuthenticationManager authenticationManager =
                httpSecurity.getSharedObject(ReactiveAuthenticationManager.class);
        return authenticationManager;
    }


}
