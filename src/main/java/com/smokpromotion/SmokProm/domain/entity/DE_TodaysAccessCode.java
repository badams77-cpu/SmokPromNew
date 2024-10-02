package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import java.time.LocalDate;

public class DE_TodaysAccessCode {

    @Column(name = "user_id")
    private int userid;

    @Column(name = "day")
    private LocalDate day;

    @Column(name = "auth_code")
    private String auth_code;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
}