package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import com.majorana.maj_orm.persist.newannot.*;
import com.majorana.maj_orm.ORM.BaseMajoranaEntity;

public abstract class BaseSmokEntity extends BaseMajoranaEntity {




    public String toString() {
        return "DE_BaseSmokEntity{" +
                "id=" + id +
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
