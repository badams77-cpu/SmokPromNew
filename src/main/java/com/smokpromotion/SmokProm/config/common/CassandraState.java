package com.smokpromotion.SmokProm.config.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

public class CassandraState {

    private boolean enabled;

    public CassandraState(boolean cassStat){
        enabled = cassStat;
    }

    public boolean isEnabbled(){
        return enabled;
    }
}
