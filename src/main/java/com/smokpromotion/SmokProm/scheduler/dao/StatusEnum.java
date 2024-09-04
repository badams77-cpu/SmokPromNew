package com.smokpromotion.SmokProm.scheduler.dao;


public enum StatusEnum {

    UNKNOWN(0), FAILED(1), SUCCESS(2);

    private int id;

    StatusEnum(int id) {
        this.id = id;
    }

    public static StatusEnum fromId(int id) {
        for (StatusEnum type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }

}
