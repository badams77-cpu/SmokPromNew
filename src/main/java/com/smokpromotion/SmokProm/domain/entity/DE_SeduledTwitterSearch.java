package com.smokpromotion.SmokProm.domain.entity;


import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import jakarta.persistence.Column;

import java.time.LocalDate;

// A Instance of call to the Twitter Search API
public class DE_SeduledTwitterSearch  extends BaseMajoranaEntity {

    private final static String TABLE_NAME="seduled_twitter_search";

    // Fields present in Majorana table.

    @Column(name="twitter_search_id")
    private int twitterSearchId;

    @Column(name="user_id")
    private int userId;

    @Column(name="results_date")
    private LocalDate resultsDate;

    @Column(name="nresult")
    private int nresults;

    @Column(name="nsent")
    private int nsent;

    public LocalDate getResultsDate() {
        return resultsDate;
    }

    public void setResultsDate(LocalDate resultsDate) {
        this.resultsDate = resultsDate;
    }

    private static final String fields = "user_id, twitter_searchid, nresult, nsent";

    public static String getTableNameStatic(){
        return TABLE_NAME;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    public int getTwitterSearchId() {
        return twitterSearchId;
    }

    public void setTwitterSearchId(int twitterSearchId) {
        this.twitterSearchId = twitterSearchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNresults() {
        return nresults;
    }

    public void setNresults(int nresults) {
        this.nresults = nresults;
    }

    public int getNsent() {
        return nsent;
    }

    public void setNsent(int nsent) {
        this.nsent = nsent;
    }

    @Override
    public String toString() {
        return "DE_SeduledTwitterSearch{" +
                "userId=" + userId +
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
