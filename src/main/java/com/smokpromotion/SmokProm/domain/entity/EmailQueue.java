package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import java.util.UUID;

public class EmailQueue {


    @Column(name="id")
    private UUID id;

    @Column(name="receipient_email")
    private String receipientEmail;

    @Column(name="title_text")
    private String titleText;

    @Column(name="body_tex")
    private String bodyText;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReceipientEmail() {
        return receipientEmail;
    }

    public void setReceipientEmail(String receipientEmail) {
        this.receipientEmail = receipientEmail;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
