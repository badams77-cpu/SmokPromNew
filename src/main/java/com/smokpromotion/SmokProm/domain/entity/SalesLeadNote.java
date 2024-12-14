package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import java.time.format.DateTimeFormatter;

public class SalesLeadNote extends BaseSmokEntity{

    private static final String TABLE_NAME = "sales_leads.sql";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    @Column(name="user_id")
    private int userId;

    @Column(name="sales_lead_id")
    private int leadEntityId;

    @Column(name="note_text")
    private String text;

    @Column(name="note_type")
    private NoteType noteType;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLeadEntityId() {
        return leadEntityId;
    }

    public void setLeadEntityId(int leadEntityId) {
        this.leadEntityId = leadEntityId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedDateString(){
        return formatter.format(created);
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    @Override
    public String toString() {
        return "SalesLeadNote{" +
                "userId=" + userId +
                ", leadEntityId=" + leadEntityId +
                ", text='" + text + '\'' +
                ", noteType=" + noteType +
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
