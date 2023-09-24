package com.smokpromotion.SmokProm.config.admin;


import org.springframework.core.convert.converter.Converter;

public class StringToPortalEnumConverter implements Converter<String, String> {
    @Override
    public String convert(String source){
        return String.valueOf(source);
    }

}
