package com.smokpromotion.SmokProm.config.admin;


import java.beans.PropertyEditorSupport;

public class PortalEnumConvertor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        String capitalized = text.toUpperCase();

    }
}

