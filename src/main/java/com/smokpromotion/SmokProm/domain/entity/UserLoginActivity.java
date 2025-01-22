package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserLoginActivity extends BaseSmokEntity {

    private static final String TABLE_NAME = "user_prec_tokens";

    @Column(name="id_user")
    private int userId;
    @Updateable
    @Column(name="token_datetime")
    private LocalDateTime tokenDate;
    @Updateable
    @Column(name="token")
    private String token;
    @Updateable
    @Column(name="locked")
    private boolean locked;

    public boolean isTokenExpired(int minutes){
        long s = ChronoUnit.SECONDS.between(tokenDate,LocalDateTime.now());
        return s>minutes*60L;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(LocalDateTime tokenDate) {
        this.tokenDate = tokenDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    public static String getTableNameStatic() {
        return TABLE_NAME;
    }


    @Override
    public String toString() {
        return "UserLoginActivity{" +
                "userId=" + userId +
                ", tokenDate=" + tokenDate +
                ", token='" + token + '\'' +
                ", id=" + id +
                ", uuid=" + uuid +
                ", deleted=" + deleted +
                ", deletedAt=" + deletedAt +
                ", createdByUserid=" + createdByUserid +
                ", updatedByUserid=" + updatedByUserid +
                ", created=" + created +
                ", updated=" + updated +
                ", createdByUserEmail='" + createdByUserEmail + '\'' +
                ", updatedByUserEmail='" + updatedByUserEmail + '\'' +
                '}';
    }
}


