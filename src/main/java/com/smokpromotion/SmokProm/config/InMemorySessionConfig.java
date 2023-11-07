package com.smokpromotion.SmokProm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.concurrent.ConcurrentHashMap;

@EnableSpringHttpSession
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
@Configuration
@Profile("memory-session")
public class InMemorySessionConfig {

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }
}
