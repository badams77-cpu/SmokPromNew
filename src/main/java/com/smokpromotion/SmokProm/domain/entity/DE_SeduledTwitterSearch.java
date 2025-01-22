package com.smokpromotion.SmokProm.domain.entity;


import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// A Instance of call to the Twitter Search API
public class DE_SeduledTwitterSearch  extends BaseMajoranaEntity {

    private final static String TABLE_NAME="seduled_twitter_search";


    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Fields present in Majorana table.

    @Column(name="twitter_search_id")
    private int twitterSearchId;

    @Column(name="user_id")
    private int userId;
    @Updateable
    @Column(name="trial_search")
    private boolean trialSearch;
    @Updateable
    @Column(name="results_date")
    private LocalDate resultsDate;
    @Updateable
    @Column(name="nresult")
    private int nresults;
    @Updateable
    @Column(name="nsent")
    private int nsent;

    public LocalDate getResultsDate() {
        return resultsDate;
    }

    public String getResultsDateString() {
        return resultsDate==null ? "" : df.format(resultsDate);
    }

    public void setResultsDate(LocalDate resultsDate) {
        this.resultsDate = resultsDate;
    }

    public boolean isTrialSearch() {
        return trialSearch;
    }

    public void setTrialSearch(boolean trialSearch) {
        this.trialSearch = trialSearch;
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
