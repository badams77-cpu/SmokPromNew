package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import jakarta.persistence.Column;

public class SearchRecordOfAPICall extends BaseMajoranaEntity {

    private static final String TABLE_NAME = "search_recond";

    @Column(name="id")
    protected int id;

    @Column(name="userid")
    private String userId;
    @Column(name="searchid")

    private String searchId;

    @Column(name="message_content")
    private String messageContent;

    @Column(name="message_text")
    private String messageText;

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}