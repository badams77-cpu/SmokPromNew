package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@org.springframework.data.cassandra.core.mapping.Table(value="smok.oauth_log")
@Table(name="smok.oauth_log ")
public class OAuthLog extends BaseSmokEntity {

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
        return uuid;
    }

    public void setId(UUID id) {
        this.uuid = id;
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