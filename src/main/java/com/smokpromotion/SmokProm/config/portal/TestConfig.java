package com.smokpromotion.SmokProm.config.portal;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Profile({"test"})
@Configuration
public class TestConfig {
    //@Bean
    //@Primary
    //public ProviderService providerService() {
    //    return Mockito.mock(ProviderService.class);
    //}
    //@Bean
    //@Primary
    //public MajoranaAccessDecisionManager MajoranaAccessDecision() {

    //        MajoranaAccessDecisionManager manager =  Mockito.mock( MajoranaAccessDecisionManager.class);
    //    when(manager.allowSection(any(),any(), any())).thenReturn(true);
    //   when(manager.allowSection(any(),any(), any(),any())).thenReturn(true);

    //    return manager;
    //}


}