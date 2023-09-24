package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.TemporalType;
import org.springframework.data.cassandra.core.mapping.Column;

import java.time.LocalDateTime;

public class HealthResult {

    @jakarta.persistence.Column(name="dbtime")
    @Column("dbtime")
    boolean status;



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
