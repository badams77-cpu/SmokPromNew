package com.smokpromotion.SmokProm.config.portal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class PortalSecurityChainConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                ReactiveAuthenticationManager reactiveAuthenticationManager) {
        final String TAG_SERVICES = "/api/**";

//          return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                  .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                  .authenticationManager(reactiveAuthenticationManager)
//                  .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
//                  .authorizeExchange(it -> it
//                          .pathMatchers(HttpMethod.POST, TAG_SERVICES).hasAnyRole("USER","ADMIN")
//                          .pathMatchers(HttpMethod.PUT, TAG_SERVICES).hasAnyRole("USER","ADMIN")
//                          .pathMatchers(HttpMethod.GET, TAG_SERVICES).hasAnyRole("USER","ADMIN")
//                          .pathMatchers(HttpMethod.DELETE, TAG_SERVICES).hasAnyRole("USER","ADMIN")
//                          .pathMatchers(TAG_SERVICES).authenticated()
//                          .anyExchange().permitAll()
//                  )
//                  .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
//                  .build();
        return null;

    }

}
