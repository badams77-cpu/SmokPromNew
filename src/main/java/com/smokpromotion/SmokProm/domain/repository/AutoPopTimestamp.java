package com.smokpromotion.SmokProm.domain.repository;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
    public @interface AutoPopTimestamp {

        boolean updated();
        boolean created();

    }
