package com.smokpromotion.SmokProm.config.portal;

public enum AuthenticationFailureReasonEnum {

    INVALID_PRINCIPAL(1, "The authentication is incorrect"),
    BLOCKED_AFTER_INVALID_ATTEMPTS(2, "User blocked after too many invalid login attempts"),
    BLOCKED_PWD_RECOVERY(3, "User blocked as password recovery in progress"),
    UNAUTHORIZED_ACCESS(3, "User is not authorized to access")
    ;

    AuthenticationFailureReasonEnum(int id, String description) {
        this.id = id;
        this.description = description;
    }

    private int id;
    private String description;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}
