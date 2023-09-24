package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.util.CookieFactory;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Profile(value = {"admin", "dxpulse_admin"})
public class AdminWebSecurityConfig implements WebSecurityConfigurer {

    // -----------------------------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------------------------

    private AdminCustomAuthenticationProvider adminCustomAuthenticationProvider;
    private CsrfTokenRepository csrfTokenRepository;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public AdminWebSecurityConfig(AdminCustomAuthenticationProvider adminCustomAuthenticationProvider,
                                  CsrfTokenRepository csrfTokenRepository,  @Value("${Majorana_COOKIE_DOMAIN:localhost}") String cookieDomain) {

        this.adminCustomAuthenticationProvider = adminCustomAuthenticationProvider;
        this.csrfTokenRepository = csrfTokenRepository;
        CookieFactory.setCookieDomain(cookieDomain);
    }

    public CsrfTokenRepository getCsrfTokenRepository() {
        return csrfTokenRepository;
    }

    public AdminCustomAuthenticationProvider getAdminCustomAuthenticationProvider() {
        return adminCustomAuthenticationProvider;
    }

    public void setAdminCustomAuthenticationProvider(AdminCustomAuthenticationProvider adminCustomAuthenticationProvider) {
        this.adminCustomAuthenticationProvider = adminCustomAuthenticationProvider;
    }

    public void setCsrfTokenRepository(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Protected Methods - WebSecurityConfigurerAdapter Overrides
    // -----------------------------------------------------------------------------------------------------------------

    // "select email, password, enabled from user where email=?"
    // "select u.email, ur.role from user_roles ur, user u where ur.user_id = u.id and u.email=?"

//
//    public void configure(
//            SecurityBuilder securityBuilder
//            //        HttpSecurity http
//    ) throws Exception {
//        HttpBasicConfigurer http = (HttpBasicConfigurer) (
//                (HttpSecurityBuilder) securityBuilder)
//                .getConfigurer(HttpBasicConfigurer.class);
//
//        http
////                .formLogin()
////                .loginPage("/login")
////                .failureUrl("/login?error")
////                .usernameParameter("email")
////                .passwordParameter("password")
////                .failureHandler(getCustomAuthenticationFailureHandler())
//                .defaultSuccessUrl("/login-handler");
////        .successHandler(sec)
////                .failureHandler(your authentication failure handler object)
////                .and()
////                .logout()
////        .logoutSuccessHandler(your logout success handler object)
////                .and()
////                .exceptionHandling()
////              .authenticationEntryPoint(new Http403ForbiddenEntryPoint());
//        http.logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
////                .logoutSuccessHandler(getCustomLogoutSuccessHandler())
//                .logoutSuccessUrl("/login").deleteCookies("JSESSIONID")
//                .clearAuthentication(true)
//                .invalidateHttpSession(true);
//        http
//                .authorizeHttpRequests((authz) -> authz.
//                        anyRequest().authenticated()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults());
//
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf().csrfTokenRepository(csrfTokenRepository);

        http.exceptionHandling().accessDeniedPage("/403");
        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web;
//    }


    @Bean
    @Profile("!test")
    public AdminAccessDecisionManager adminAccessDecision() {

        return new AdminAccessDecisionManager();
    }

    @Bean
    public AdminLogoutSuccessHandler getCustomLogoutSuccessHandler() {
        return new AdminLogoutSuccessHandler();
    }

    @Bean
    public AdminAuthenticationFailureHandler getCustomAuthenticationFailureHandler(){
        AdminAuthenticationFailureHandler customAuthenticationFailureHandler = new AdminAuthenticationFailureHandler();
        return customAuthenticationFailureHandler;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Override
    public void init(SecurityBuilder builder) throws Exception {

    }

    @Override
    public void configure(SecurityBuilder builder) throws Exception {

    }

}