package com.smokpromotion.SmokProm.form;

import com.urcompliant.domain.PortalEnum;

/**
 * Class backing a change password form.
 */
public class ChangePasswordForm {

    private String current;
    private String neww;
    private String repeat;
    private int userId;
    private String portalAndUser;
    private boolean policy = false;

    // ---------------------------------------------------------------------------------------------
    // Public Methods - Accessors & Mutators (Generated)
    // ---------------------------------------------------------------------------------------------

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getNeww() {
        return neww;
    }

    public void setNeww(String neww) {
        this.neww = neww;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isPolicy() {
        return policy;
    }

    public void setPolicy(boolean policy) {
        this.policy = policy;
    }

    public String getPortalAndUser() {
        return portalAndUser;
    }

    public void setPortalAndUser(String portalAndUser) {
        this.portalAndUser = portalAndUser;
    }

    // ---------------------------------------------------------------------------------------------
    // Public Methods - toString
    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "ChangePasswordForm{" +
                "current='" + current + '\'' +
                ", neww='" + neww + '\'' +
                ", repeat='" + repeat + '\'' +
                ", userId=" + userId +
                ", portalAndUser='" + portalAndUser + '\'' +
                ", portal=" + portal +
                ", policy=" + policy +
                '}';
    }

}
