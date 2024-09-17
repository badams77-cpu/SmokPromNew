package com.smokpromotion.SmokProm.domain.entity;

import com.smokpromotion.SmokProm.util.SecVnEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.Column;
//import org.springframework.data.cassandra.core.mapping.Column;



public class AdminUser  extends BaseSmokEntity  {

    private static final String TABLE_NAME = "admin_user";

//    @org.springframework.data.cassandra.core.mapping.Column("username")
    @Column(name="username")
    private String username;
//    @org.springframework.data.cassandra.core.mapping.Column("firstname")
    @Column(name="firstname")
    private String firstname;
//    @org.springframework.data.cassandra.core.mapping.Column("lastname")
    @Column(name="lastname")
    private String lastname;
    //   @org.springframework.data.cassandra.core.mapping.Column("passwd")
    @Column(name="passwd")
    private String userpw;
    @Column(name="secVn")
    //@org.springframework.data.cassandra.core.mapping.Column("secVn")
    private int secVn;
    //@org.springframework.data.cassandra.core.mapping.Column("last_login")
    @Column(name="last_login")
    private LocalDateTime lastvisit;
    @Column(name="useractive")
//    @org.springframework.data.cassandra.core.mapping.Column("useractive")
    private boolean useractive;
//    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token")
    @Column(name="change_pass_token")
    private String changePassToken;

//    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token_created")
    @Column(name="change_pass_token_created")
    private LocalDateTime changePassTokenCreate;

    public static String getTableNameStatic(){
        return TABLE_NAME;
    }

    public AdminUser(){

    }


    public String getTableName(){
        return TABLE_NAME;
    }


    public String getChangePassToken() {
        return changePassToken;
    }

    public void setChangePassToken(String changePassToken) {
        this.changePassToken = changePassToken;
    }

    public LocalDateTime getChangePassTokenCreate() {
        return changePassTokenCreate;
    }

    public void setChangePassTokenCreate(LocalDateTime changePassTokenCreate) {
        this.changePassTokenCreate = changePassTokenCreate;
    }

    public String getIdString(){
        return uuid==null ? ""+id : uuid.toString();
    }

/*
    public DAdminUser(UserForm userForm) {

        this.firstname = userForm.getFirstname();
        this.lastname = userForm.getLastname();
        this.username = userForm.getEmail();
        this.organization = AdminUserOrgEnum.getByCode(userForm.getOrganization());
        this.language = userForm.getLanguage();
        this.useractive = userForm.isUseractive();
        this.isManageAdminRoles = userForm.isManageAdminRoles();
    }

*/
    public String getLastvisitFormatted() {
        String last = "Never";
        if (lastvisit != null) {
            last = lastvisit.format(DateTimeFormatter.ofPattern("dd-MMM-yy HH-mm-ss"));
        }
        return last;
    }

//    public String getLanguageFormatted() {
//        String lang = "";
//        if (language != null && !language.isEmpty()) {
//            lang = language.replace("-", " ");
//            lang = WordUtils.capitalize(lang);
//        }
//        return lang;
//       }

//    public String getUseractiveFormatted() {
//        return useractive ? "Yes" : "No";
//    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpw() {
        return userpw;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        deleted = deleted;
    }


    public int getSecVn() {
        return secVn;
    }

    public void setSecVn(int sec_vn) {
        this.secVn = sec_vn;
    }

    public SecVnEnum getSecVNEnum() {
        return SecVnEnum.getFromCode(secVn);
    }

    /*
    public boolean isUseractive() {
        return useractive;
    }

    public void setUseractive(boolean useractive) {
        this.useractive = useractive;
    }

     */

    public LocalDateTime getLastvisit() {
        return lastvisit;
    }

    public void setLastvisit(LocalDateTime lastvisit) {
        this.lastvisit = lastvisit;
    }

    /*
    public boolean isManageAdminRoles() {
        return isManageAdminRoles;
    }

    public void setManageAdminRoles(boolean manageAdminRoles) {
        isManageAdminRoles = manageAdminRoles;
    }
       */

    @Override
    public String toString() {
        return "AdminUser{" +
                "username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", userpw='" + userpw + '\'' +
                ", secVn=" + secVn +
                ", lastvisit=" + lastvisit +
                ", useractive=" + useractive +
                ", changePassToken='" + changePassToken + '\'' +
                ", changePassTokenCreate=" + changePassTokenCreate +
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
