package com.smokpromotion.SmokProm.exceptions;

public class NotLoggedInException extends Exception {

    private String message;

    public NotLoggedInException( String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
