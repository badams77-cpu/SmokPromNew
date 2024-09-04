package com.smokpromotion.SmokProm.Exceptions;

public class ServerError {

    private String message;

    public ServerError(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
