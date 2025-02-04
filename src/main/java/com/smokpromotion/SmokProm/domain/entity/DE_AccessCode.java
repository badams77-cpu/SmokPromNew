package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DE_AccessCode extends BaseMajoranaEntity {

    private static final String TABLE_NAME = "access_codes";
    @Updateable
    @Column(name="access_code")
    private String accessCode;

    @Column(name="user_id")
    private int userId;
    @Updateable
    @Column(name="request_token")
    private String requestToken;
    @Updateable
    @Column(name="code_date")
    private LocalDateTime codeDate;
    @Updateable
    @Column(name="access_code_date")
    private LocalDateTime accessCodeDate;
    @Updateable
    @Column(name="access_code_used_date")
    private LocalDateTime codeUsedDate;

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getAccessCodeDate() {
        return accessCodeDate;
    }

    public LocalDateTime getCodeDate() {
        return codeDate;
    }

    public void setCodeDate(LocalDateTime codeDate) {
        this.codeDate = codeDate;
    }

    public void setAccessCodeDate(LocalDateTime codeDate) {
        this.accessCodeDate = codeDate;
    }

    public LocalDateTime getCodeUsedDate() {
        return codeUsedDate;
    }

    public void setCodeUsedDate(LocalDateTime codeUsedDate) {
        this.codeUsedDate = codeUsedDate;
    }
}
