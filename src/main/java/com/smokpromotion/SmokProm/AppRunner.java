package com.smokpromotion.SmokProm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    private static final String PROFILE_ADMIN = "admin";
    private static final String PROFILE_APP = "smok_app";
    private static final String PROFILE_DEMON = "smok_demon";
    private static final String PROFILE_INIT = "smok_init";

    @Autowired
    Environment env;

    @Value("${smok.admin.version:0.1.0}")
    private String smokAdminVariantString;
    @Value("${smok.admin.version:0.1.0}")
    private String smokAdminVersionString;

    @Value("${smok.app.version:0.1.0}")
    private String smokPortalVersionString;

    @Value("${smok.demon.version:0.1.0}")
    private String smokDemonVersionString;

    @Value("${smok.init.version:0.1.0}")
    private String smokInitVersionString;

    @Value("${smok.searchrunner.version:0.1.0}")
    private String smpkSearchRunneVersionString;
    
    public void run(ApplicationArguments args) {

        Set<String> activeProfiles = Arrays.stream(env.getActiveProfiles()).collect(Collectors.toSet());

        String smokAppVariantDesc = "Unknown";
        String smokAppVersionDesc = "Unknown";

        if (activeProfiles.contains(PROFILE_APP)) {
            smokAppVariantDesc = PROFILE_APP;
            smokAppVersionDesc = smokPortalVersionString;
        } else if (activeProfiles.contains(PROFILE_DEMON)) {
            smokAppVariantDesc = PROFILE_DEMON;
            smokAppVersionDesc = smokDemonVersionString;
        } else if (activeProfiles.contains(PROFILE_INIT)) {
            smokAppVariantDesc = PROFILE_INIT;
            smokAppVersionDesc = smokInitVersionString;
        } else if (activeProfiles.contains(PROFILE_ADMIN)) {
                smokAppVariantDesc = PROFILE_ADMIN;
                smokAppVersionDesc = smokAdminVersionString;
        } else {
            LOGGER.warn(String.format("run: No known smok app profiles used, active profiles are: %s", activeProfiles.stream().collect(Collectors.joining(","))));
        }

        LOGGER.warn(String.format("smok %s Application startup completed - version: %s", smokAppVariantDesc, smokAppVersionDesc));

    }


}
