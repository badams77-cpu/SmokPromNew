package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class OAuthLog extends BaseSmokEntity {

    @Column(name="!id")
    private UUID id;
    @Column(name="!username")
    private String username;
    @Column(name="!log_Date")
    private LocalDateTime logDate;
    @Column(name="!oauth_statuss")
    private String errorMessage;


    public int getid() {
        return uuid.hashCode();
    }

    public UUID getUuid() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OAuthLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", logDate=" + logDate +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}