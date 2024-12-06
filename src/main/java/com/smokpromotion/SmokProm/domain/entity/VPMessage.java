package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;

import com.smokpromotion.SmokProm.form.UserForm;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VPMessage extends BaseMajoranaEntity implements Serializable {

    private static final String TABLE_NAME="vpmessages";

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Column(name="message")
    private String message;
    @Column(name="from_user")
    private int from;
    @Column(name="to_user")
    private int to;
    @Column(name="reply_to")
    private int replyTo;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public String getCreatedDate(){
            if (created==null){ return "";}
            return df.format(created);
    )

    @Override
    public String toString() {
        return "VPMessage{" +
                "message='" + message + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", replyTo=" + replyTo +
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

    @Override
    public LocalDateTime getCreated() {
        return super.getCreated();
    }
}