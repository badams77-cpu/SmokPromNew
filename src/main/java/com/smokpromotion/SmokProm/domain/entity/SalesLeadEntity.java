package com.smokpromotion.SmokProm.domain.entity;

import jakarta.persistence.Column;

import java.time.format.DateTimeFormatter;

public class SalesLeadEntity extends BaseSmokEntity{

    private static final String TABLE_NAME = "sales_leads";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Column(name="user_id")
    private int userId;

    @Column(name="twitter_handle")
    private String twitterHandle;

    @Column(name="twitter_user_id")
    private long twitterUserId;

    @Column(name="leadStatus")
    private LeadStatus leadStatus;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(long twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public LeadStatus getLeadStatus() {
        return leadStatus;
    }

    public String getLeadStatusString() {
        return leadStatus==null ? "UKNOWN":leadStatus.getType();
    }

    public String getCreatedDateString(){
        return formatter.format(created);
    }

    public void setLeadStatus(LeadStatus leadStatus) {
        this.leadStatus = leadStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SalesLeadEntity{" +
                "userId=" + userId +
                ", twitterHandle='" + twitterHandle + '\'' +
                ", twitterUserId=" + twitterUserId +
                ", leadStatus=" + leadStatus +
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
