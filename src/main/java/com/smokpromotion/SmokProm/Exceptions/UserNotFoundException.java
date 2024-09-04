package com.smokpromotion.SmokProm.Exceptions;

public class UserNotFoundException extends Exception {

    private String userName;

    public UserNotFoundException(String username){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}

