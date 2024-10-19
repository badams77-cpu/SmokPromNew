package com.smokpromotion.SmokProm.config.entity;

import java.time.LocalDateTime;

public class AnalyticsUserLogins {

   private int userid;
   private String userEmail;
   private int logins;
   private LocalDateTime lastLoginDate;
   private LocalDateTime loginDate;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getLogins() {
        return logins;
    }

    public void setLogins(int logins) {
        this.logins = logins;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(LocalDateTime loginDate) {
        this.loginDate = loginDate;
    }

    @Override
    public String toString() {
        return "AnalyticsUserLogins{" +
                "userid=" + userid +
                ", userEmail='" + userEmail + '\'' +
                ", logins=" + logins +
                ", lastLoginDate=" + lastLoginDate +
                ", loginDate=" + loginDate +
                '}';
    }
}
