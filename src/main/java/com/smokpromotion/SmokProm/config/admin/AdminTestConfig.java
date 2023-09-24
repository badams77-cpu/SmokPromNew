package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.config.portal.MajoranaAccessDecisionManager;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
//
//@Profile("test")
//@Configuration
public class AdminTestConfig {
//
//
//    @Bean
//    @Primary
//    @Profile("admin")
//    public AdminAccessDecisionManager adminAccessDecision() {
//
//        AdminAccessDecisionManager manager =  Mockito.mock( AdminAccessDecisionManager.class);
//        when(manager.allowSection(any(),any(), any())).thenReturn(true);
//        when(manager.allowSection(any(),any(), any(),any())).thenReturn(true);
//        when(manager.allowSection(any(),any(), any(),any(),any())).thenReturn(true);
//        return manager;
//    }
//
//
////    @Bean
////       @Primary
////    @Profile("admin")
////    public ProviderService providerService() {
////        return Mockito.mock(ProviderService.class);
////    }
//
}