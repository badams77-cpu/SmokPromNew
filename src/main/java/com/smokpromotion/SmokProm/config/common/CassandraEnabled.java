package com.smokpromotion.SmokProm.config.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(value="!mock_cass")
public class CassandraEnabled {

    @Bean
    public CassandraState cassandraState() {
        return new CassandraState(true);
    }

}
