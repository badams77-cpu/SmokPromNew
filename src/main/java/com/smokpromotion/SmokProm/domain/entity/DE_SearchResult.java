package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import jakarta.persistence.Column;

public class DE_SearchResult extends BaseMajoranaEntity {

    private final static String TABLE_NAME="seduled_twitter_search";

    @Column(name="search_id")
    private int searchId;

    @Column(name="user_id")
    private int userId;

    @Column(name="tweet_id")
    private long tweetId;

    @Column(name="twitter_user_hande")
    private String twitterUserHandle;

    @Column(name="seduled_search_id")
    private int seduledSearchNumber;
    // Results for the given Tweeter Search Segedial Set
    ;


    public static String getTableNameStatic(){
        return TABLE_NAME;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    private static final String fields = "search_id, user_id, twitter_user_handler," +
            " seduled_search_id ";

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTwitterUserHandle() {
        return twitterUserHandle;
    }

    public void setTwitterUserHandle(String twitterUserHandler) {
        this.twitterUserHandle = twitterUserHandler;
    }

    public int getSeduledSearchNumber() {
        return seduledSearchNumber;
    }

    public void setSeduledSearchNumber(int seduledSearchNumber) {
        this.seduledSearchNumber = seduledSearchNumber;
    }

    @Override
    public String toString() {
        return "DE_SearchResult{" +
                "searchId='" + searchId + '\'' +
                ", userId='" + userId + '\'' +
                ", twitterUserHandle='" + twitterUserHandle + '\'' +
                ", seduledSearchNumber=" + seduledSearchNumber +
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