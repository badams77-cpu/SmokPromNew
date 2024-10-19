package com.smokpromotion.SmokProm.config.common;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PrimaryGlobalDBName {



    private static final String MAIN_DB = "maindb";

    public String getMainDb(){
        return MAIN_DB;
    }

}
