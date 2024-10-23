package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.majorana.maj_orm.persist.newannot.Updateable;
import jakarta.persistence.Column;
import javax.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twitter4j.v1.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DE_TwitterSearch extends BaseMajoranaEntity {

    private final static String TABLE_NAME="twitter_search";

//    @Column(name="id")
//    protected int id;

    @Column(name="userid")
    private int userId;


    @Updateable
    @Column(name="result_date")
    private LocalDate resultDate;

    @Size(min=2, max=80)

    @Updateable
    @Column(name="search_text")
    private String searchText;


    @Size(min=0, max=250)
    @Column(name="message")
    @Updateable
    private String message;


    @Updateable
    @Size(min=0, max=250)
    @Column(name="tweet_text")
    private String text;

    @Updateable
    @Column(name="active")
    private boolean active;


    private static final String fields = "userid, searchid, search_text, active, results_date, message, text";

    public static String getTableNameStatic(){
        return TABLE_NAME;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    public String lastSearchText(){
        if (resultDate==null){ return "Never"; }
        return resultDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }


    public LocalDate getResultDate() {
        return resultDate;
    }

    public void setResultDate(LocalDate resultDate) {
        this.resultDate = resultDate;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }




    @Override
    public String toString() {
        return "DE_TwitterSearch{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", resultDate=" + resultDate +
                ", searchText='" + searchText + '\'' +
                ", message='" + message + '\'' +
                ", text='" + text + '\'' +
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