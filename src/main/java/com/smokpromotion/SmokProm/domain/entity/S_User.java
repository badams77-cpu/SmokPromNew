package com.smokpromotion.SmokProm.domain.entity;

import com.smokpromotion.SmokProm.domain.repository.Updateable;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@org.springframework.data.cassandra.core.mapping.Table(value="smok.user")
@Table(name="smok.user")
public class S_User extends BaseSmokEntity implements Serializable {

    // Fields present in Majorana table.


    @Column(name="username")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("username")
    private String username;
  @Column(name="firstname")
  @Updateable
  @org.springframework.data.cassandra.core.mapping.Column("firstname")
  private String firstname;
  @Column(name="lastname")
  @Updateable
  @org.springframework.data.cassandra.core.mapping.Column("lastname")
  private String lastname;
    @Column(name="secVn")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("secVn")
    private int secVn;

    @Column(name="subscription_level")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("subscription_level")
    private String subscriptionAccessLevel;
    @org.springframework.data.cassandra.core.mapping.Column("passwd")
    @Column(name="passwd")
    @Updateable
    private String userpw;

    @org.springframework.data.cassandra.core.mapping.Column("company_name")
    @Column(name="company_name")
    @Updateable
    private String companyName;
    @org.springframework.data.cassandra.core.mapping.Column("cc_email")
    @Column(name="cc_email")
    @Updateable
    private String ccEmail;
    @org.springframework.data.cassandra.core.mapping.Column("address1")
    @Column(name="address1")
    @Updateable
    private String address1;
    @org.springframework.data.cassandra.core.mapping.Column("address2")
    @Column(name="address2")
    @Updateable
    private String address2;
    @Column(name="town")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("town")
    private String town;
    @org.springframework.data.cassandra.core.mapping.Column("country")
    @Column(name="country")
    @Updateable
    private String country;
    @org.springframework.data.cassandra.core.mapping.Column("postcode")
    @Column(name="postcode")
    @Updateable
    private String postcode;
    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token")
    @Column(name="change_pass_token")
    @Updateable
    private String changePassToken;
    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token_created")
    @Column(name="change_pass_token_created")
    @Updateable
    private LocalDateTime changePassTokenCreate;

    @org.springframework.data.cassandra.core.mapping.Column("twitter_handler")
    @Column(name="twitter_handler")
    @Updateable
    private String twitterHandle;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_reg_token")
    @Column(name="oauth_reg_token")
    @Updateable
    private String oauthRegToken;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_reg_secret")
    @Column(name="oauth_reg_secret")
    @Updateable
    private String oauthRegSecret;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_verifier")
    @Column(name="oauth_verifier")
    @Updateable
    private String oauthVerifier;

    @org.springframework.data.cassandra.core.mapping.Column("access_token")
    @Column(name="access_token")
    @Updateable
    private String twitterAccessCode;

    @org.springframework.data.cassandra.core.mapping.Column("access_token_expiry")
    @Column(name="access_token_expiry")
    @Updateable
    private LocalDateTime accessTokenExpiry;

    @Column(name="useractive")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("useractive")
    private boolean useractive;

    @Column(name="last_login")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("last_login")
    private LocalDateTime lastVisit;

    @Column(name="passwd_change_date")
    @Updateable
    @org.springframework.data.cassandra.core.mapping.Column("passwd_change_date")
    private LocalDateTime passwdChangeDate;




    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public S_User() {
    }

    public String getSubscriptionAccessLevel() {
        return subscriptionAccessLevel;
    }

    public void setSubscriptionAccessLevel(String subscriptionAccessLevel) {
        this.subscriptionAccessLevel = subscriptionAccessLevel;
    }


    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getSecVn() {
        return secVn;
    }

    public void setSecVn(int secVn) {
        this.secVn = secVn;
    }

    public String getUserpw() {
        return userpw;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCcEmail() {
        return ccEmail;
    }

    public void setCcEmail(String ccEmail) {
        this.ccEmail = ccEmail;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getOauthRegToken() {
        return oauthRegToken;
    }

    public void setOauthRegToken(String oauthRegToken) {
        this.oauthRegToken = oauthRegToken;
    }

    public String getOauthRegSecret() {
        return oauthRegSecret;
    }

    public void setOauthRegSecret(String oauthRegSecret) {
        this.oauthRegSecret = oauthRegSecret;
    }

    public String getOauthVerifier() {
        return oauthVerifier;
    }

    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }

    public String getTwitterAccessCode() {
        return twitterAccessCode;
    }

    public void setTwitterAccessCode(String twitterAccessCode) {
        this.twitterAccessCode = twitterAccessCode;
    }

    public LocalDateTime getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(LocalDateTime accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public boolean isUseractive() {
        return useractive;
    }

    public void setUseractive(boolean useractive) {
        this.useractive = useractive;
    }

    public LocalDateTime getPasswdChangeDate() {
        return passwdChangeDate;
    }

    public void setPasswdChangeDate(LocalDateTime passwdChangeDate) {
        this.passwdChangeDate = passwdChangeDate;
    }

    public String getIdString(){
        return uuid==null ? ""+id : uuid.toString();
    }

    @Override
    public String toString() {
        return "S_User{" +
                "username='" + username + '\'' +
               ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", secVn=" + secVn +
                ", userpw='" + userpw + '\'' +
                ", lastVisit=" + lastVisit +
                ", companyName='" + companyName + '\'' +
                ", ccEmail='" + ccEmail + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", town='" + town + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                ", changePassToken='" + changePassToken + '\'' +
                ", changePassTokenCreate=" + changePassTokenCreate +
                ", twitterHandle='" + twitterHandle + '\'' +
                ", oauthRegToken='" + oauthRegToken + '\'' +
                ", oauthRegSecret='" + oauthRegSecret + '\'' +
                ", oauthVerifier='" + oauthVerifier + '\'' +
                ", twitterAccessCode='" + twitterAccessCode + '\'' +
                ", accessTokenExpiry=" + accessTokenExpiry +
                ", useractive=" + useractive +
                ", passwdChangeDate=" + passwdChangeDate +
                '}';
    }
}
