package com.smokpromotion.SmokProm.domain.entity;

import com.smokpromotion.SmokProm.domain.entity.BaseSmokEntity;
import com.smokpromotion.SmokProm.domain.repository.AutoPopTimestamp;
import com.smokpromotion.SmokProm.domain.repository.Updateable;
import jakarta.persistence.Column;

import jakarta.persistence.Column;
import jakarta.persistence.PrimaryKeyJoinColumn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Id;

public class BaseSmokEntity {


    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 0, name = "id")
    @Id()
    @Column(name="id")
    protected int id;
    @org.springframework.data.cassandra.core.mapping.Column("id")
    @Column(name="uuid")
    protected UUID uuid;
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("deleted")
    @Column(name="deleted")
    protected boolean deleted;
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("deletedAt")
    @Column(name="deleted_at")
    protected LocalDateTime deletedAt;
    @org.springframework.data.cassandra.core.mapping.Column("created_by_userid")
    @Column(name="created_by_userid")
    protected int createdByUserid;
    @org.springframework.data.cassandra.core.mapping.Column("update_by_userid")
    @Column(name="updated_by_userid")
    protected int updatedByUserid;
    @Updateable
    @AutoPopTimestamp(updated = false , created = true)
    @org.springframework.data.cassandra.core.mapping.Column("created")
    @Column(name="created")
    protected LocalDateTime created;
    @Updateable
    @AutoPopTimestamp(updated = false , created = true)
    @org.springframework.data.cassandra.core.mapping.Column("updated")
    @Column(name="updated")
    protected LocalDateTime updated;
    @Column(name="created_by_useremail")
    @org.springframework.data.cassandra.core.mapping.Column("created_by_useremail")
    protected transient String createdByUserEmail;
    @org.springframework.data.cassandra.core.mapping.Column("updated_by_useremail")
    @Column(name="updated_by_useremail")
    protected transient String updatedByUserEmail;


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getCreatedByUserid() {
        return createdByUserid;
    }

    public void setCreatedByUserid(int createdByUserid) {
        this.createdByUserid = createdByUserid;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getCreatedByUserEmail() {
        return createdByUserEmail;
    }

    public void setCreatedByUserEmail(String createdByUserEmail) {
        this.createdByUserEmail = createdByUserEmail;
    }


    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getUpdatedByUserid() {
        return updatedByUserid;
    }

    public void setUpdatedByUserid(int updatedByUserid) {
        this.updatedByUserid = updatedByUserid;
    }

    public String getUpdatedByUserEmail() {
        return updatedByUserEmail;
    }

    public void setUpdatedByUserEmail(String updatedByUserEmail) {
        this.updatedByUserEmail = updatedByUserEmail;
    }

    public String toString() {
        return "DE_BaseCostEntity{" +
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
