package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;

import java.time.LocalDate;

public class DE_TodaysAccessCode extends BaseMajoranaEntity {

    public static final String TABLE_NAME="todays_access_code";

    @Column(name = "user_id")
    private int userid;
    @Updateable
    @Column(name = "day")
    private LocalDate day;
    @Updateable
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

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}