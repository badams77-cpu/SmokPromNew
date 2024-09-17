package com.smokpromotion.SmokProm.analytics.entity;

import com.smokpromotion.SmokProm.domain.entity.BaseSmokEntity;

import java.util.UUID;

public class AnalyticTokens extends BaseSmokEntity {

    private final static String TABLE_NAME = "analytics_tokens";

    private UUID token;

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getTableName(){
        return TABLE_NAME;
    }
}
