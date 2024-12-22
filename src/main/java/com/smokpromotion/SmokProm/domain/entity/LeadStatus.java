package com.smokpromotion.SmokProm.domain.entity;

import java.util.Arrays;

public enum LeadStatus {

    UKNOWN("unknown"),NEW("new"), FAILED("failed"), SUCCESS("success"), LAPSED("lapsed");

    private String type;

    LeadStatus(String s){
        type=s;
    }

    public static LeadStatus fromString(String s){
        return Arrays.stream(values())
                .filter(x->x.getType().equalsIgnoreCase(s))
                .findFirst().orElse(UKNOWN);
    }


    public String getType() {
        return type;
    }

}
