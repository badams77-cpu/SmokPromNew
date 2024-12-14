package com.smokpromotion.SmokProm.domain.entity;

import java.util.Arrays;

public enum NoteType {

    UKNOWN(""),LIKES("likes"), DISLIKES("dislikes"), WANTS("wants");

    private String type;

    NoteType(String s){
        type=s;
    }

    public static NoteType fromString(String s){
        return Arrays.stream(values())
                .filter(x->x.getType().equalsIgnoreCase(s))
                .findFirst().orElse(UKNOWN);
    }

    public String getType() {
        return type;
    }

}
