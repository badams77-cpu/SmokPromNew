package com.smokpromotion.SmokProm.domain.entity;


import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import jakarta.persistence.Column;

import java.util.Arrays;
import java.util.Optional;

public class DE_EmailTemplate extends BaseMajoranaEntity {

    private static final String TABLE_NAME = "email_templates";

    @Column(name="name")
    private String name;
    @Column(name="template")
    private String templateBody;
    @Column(name="subject")
    private String subject;
    @Column(name="language")
    private String language;


    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    @Override
    public String getTableName() {
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateBody() {
        return templateBody;
    }

    public void setTemplateBody(String templateBody) {
        this.templateBody = templateBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageLabel(){
        Optional<EmailLanguage> lang = Arrays.stream(EmailLanguage.values()).filter(x->x.getValue().equals(getLanguage())).findFirst();
        return lang.map(EmailLanguage::getLabel).orElse("Unknown");
    }

    @Override
    public String toString() {
        return "DE_EmailTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", templateBody='" + (templateBody.length()>100 ? templateBody.substring(0,100) : templateBody) + '\'' +
                ", subject='" + subject + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
