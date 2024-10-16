package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import jakarta.persistence.Column;

import java.util.UUID;

public class EmailQueue extends BaseMajoranaEntity {

  private static final String TABLE_NAME = "email_queue";

  @Column(name="receipient_email")
  private String receipientEmail;

  @Column(name="title_text")
  private String titleText;

  @Column(name="body_text")
  private String bodyText;


  public static String getTableNameStatic() {
    return TABLE_NAME;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  public String getReceipientEmail() {
    return receipientEmail;
  }

  public void setReceipientEmail(String receipientEmail) {
    this.receipientEmail = receipientEmail;
  }

  public String getTitleText() {
    return titleText;
  }

  public void setTitleText(String titleText) {
    this.titleText = titleText;
  }

  public String getBodyText() {
    return bodyText;
  }

  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  @Override
  public String toString() {
    return "EmailQueue{" +
            "receipientEmail='" + receipientEmail + '\'' +
            ", titleText='" + titleText + '\'' +
            ", bodyText='" + bodyText + '\'' +
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
