package com.smokpromotion.SmokProm.config.portal;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ProviderMock {

//    ProviderService providerService;
//
//    @Bean
//    @Primary
//    public ProviderService providerService(){
//        providerService = Mockito.mock(ProviderService.class);
//        return providerService;
//    }
//
//    public ProviderService getProviderService() {
//        return providerService;
//    }
}
