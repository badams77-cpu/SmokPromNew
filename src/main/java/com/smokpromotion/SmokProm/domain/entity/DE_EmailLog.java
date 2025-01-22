package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;

public class DE_EmailLog extends BaseMajoranaEntity {


    private static final String TABLE_NAME = "email_log";

    @Column(name="userid")
    private String userid;
    @Updateable
    @Column(name="error_message")
    private String errorMessage;
    @Updateable
    @Column(name="status")
    private String status;



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTableNameStatic() {
        return TABLE_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String toString() {
        return "DE_EmailLog{" +
                "userid='" + userid + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", status='" + status + '\'' +
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