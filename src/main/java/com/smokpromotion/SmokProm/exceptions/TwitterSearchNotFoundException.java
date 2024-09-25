package com.smokpromotion.SmokProm.exceptions;

public class TwitterSearchNotFoundException extends Exception {

    private String username;
    private String message;

    public TwitterSearchNotFoundException(String username, String message){
        this.username = username;
        this.message = message;
    }

    public TwitterSearchNotFoundException(String username, Exception e){
        this.username = username;
        this.message = "Exception finding user "+e.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
