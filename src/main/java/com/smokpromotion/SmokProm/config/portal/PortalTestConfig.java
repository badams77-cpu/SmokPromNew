package com.smokpromotion.SmokProm.config.portal;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Profile("test")
@Configuration
public class PortalTestConfig {



    @Bean
    @Primary
    public MajoranaAccessDecisionManager MajoranaAccessDecision() {

        MajoranaAccessDecisionManager manager =  Mockito.mock( MajoranaAccessDecisionManager.class);
        when(manager.allowSection(any(),any(), any())).thenReturn(true);
        when(manager.allowSection(any(),any(), any(),any())).thenReturn(true);

        return manager;
    }


}