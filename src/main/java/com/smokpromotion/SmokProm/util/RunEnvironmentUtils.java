package com.smokpromotion.SmokProm.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RunEnvironmentUtils {

    private static final String DENTRIX_PROFILE = "dxpulse_portal";
    private static final String DENTRIX_ADMIN = "dxpulse_admin";
    private static final String TEST_PROFILE = "test";

    @Autowired
    Environment env;

    public boolean isDentrix(){
        return Arrays.stream(env.getActiveProfiles()).anyMatch(x->x.equals(DENTRIX_PROFILE) || x.equals(DENTRIX_ADMIN));
    }

    public boolean isTest(){
        return Arrays.stream(env.getActiveProfiles()).anyMatch(x->x.equals(TEST_PROFILE));
    }

}
