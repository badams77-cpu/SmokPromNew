package com.smokpromotion.SmokProm.domain.entity;

import com.smokpromotion.SmokProm.form.UserForm;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@org.springframework.data.cassandra.core.mapping.Table(value="smok.user")
@Table(name="smok.user")
public class S_User extends BaseSmokEntity implements Serializable {

    // Fields present in Majorana table.
    private static final String TABLE_NAME = "user";

    @Column(name="username")
    @org.springframework.data.cassandra.core.mapping.Column("username")
    private String username;
  @Column(name="firstname")
  @org.springframework.data.cassandra.core.mapping.Column("firstname")
  private String firstname;
    @Column(name="stripe_payments_active")
    private int subCount;
  @Column(name="lastname")
  @org.springframework.data.cassandra.core.mapping.Column("lastname")
  private String lastname;
    @Column(name="secVn")
    @org.springframework.data.cassandra.core.mapping.Column("secVn")
    private int secVn;
    @org.springframework.data.cassandra.core.mapping.Column("passwd")
    @Column(name="passwd")
    private String userpw;
    @org.springframework.data.cassandra.core.mapping.Column("last_login")
    @Column(name="last_login")
    private LocalDateTime lastVisit;
    @org.springframework.data.cassandra.core.mapping.Column("company_name")
    @Column(name="company_name")
    private String companyName;
    @org.springframework.data.cassandra.core.mapping.Column("cc_email")
    @Column(name="cc_email")
    private String ccEmail;
    @org.springframework.data.cassandra.core.mapping.Column("address1")
    @Column(name="address1")
    private String address1;
    @org.springframework.data.cassandra.core.mapping.Column("address2")
    @Column(name="address2")
    private String address2;
    @Column(name="town")
    @org.springframework.data.cassandra.core.mapping.Column("town")
    private String town;
    @org.springframework.data.cassandra.core.mapping.Column("country")
    @Column(name="country")
    private String country;
    @org.springframework.data.cassandra.core.mapping.Column("postcode")
    @Column(name="postcode")
    private String postcode;
    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token")
    @Column(name="change_pass_token")
    private String changePassToken;
    @org.springframework.data.cassandra.core.mapping.Column("change_pass_token_created")
    @Column(name="change_pass_token_created")
    private LocalDateTime changePassTokenCreate;

    @org.springframework.data.cassandra.core.mapping.Column("twitter_handler")
    @Column(name="twitter_handler")
    private String twitterHandle;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_reg_token")
    @Column(name="oauth_reg_token")
    private String oauthRegToken;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_reg_secret")
    @Column(name="oauth_reg_secret")
    private String oauthRegSecret;

    @org.springframework.data.cassandra.core.mapping.Column("oauth_verifier")
    @Column(name="oauth_verifier")
    private String oauthVerifier;

    @org.springframework.data.cassandra.core.mapping.Column("access_token")
    @Column(name="access_token")
    private String twitterAccessCode;

    @org.springframework.data.cassandra.core.mapping.Column("access_token_expiry")
    @Column(name="access_token_expiry")
    private LocalDateTime accessTokenExpiry;

    @Column(name="useractive")
    @org.springframework.data.cassandra.core.mapping.Column("useractive")
    private boolean useractive;

    @Column(name="passwd_change_date")
    @org.springframework.data.cassandra.core.mapping.Column("passwd_change_date")
    private LocalDateTime passwdChangeDate;


    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------


    public S_User() {
    }

    public S_User(UserForm uf) {
      username = uf.getEmail();
      address1 = uf.getAddress1();
      address2 = uf.getAddress2();
      town = uf.getTown();
      country = uf.getCountry();
      userpw = uf.getPassword();
      firstname = uf.getFirstname();
      lastname = uf.getLastname();
      postcode = uf.getPostcode();

      useractive = true;
    }


    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public static String getTableNameStatic(){
        return TABLE_NAME;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    public SecVnEnum getSecVNEnum() {
        return SecVnEnum.getFromCode(secVn);
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

    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
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
