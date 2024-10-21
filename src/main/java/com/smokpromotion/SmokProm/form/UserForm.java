package com.smokpromotion.SmokProm.form;

import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class backing the "Your Profile" form offered to end users.
 */
public class UserForm {

    private String id;
    @Size(min=1, max=40)
    private String firstname;
    @Size(min=1, max=40)
    private String lastname;
    @Size(min=1, max=40)
    private String address1;
    @Size(min=1, max=40)
    private String address2;
    @Size(min=1, max=40)
    private String town;
    @Size(min=1, max=40)
    private String country;
    @Size(min=1, max=12)
    private String postcode;
    @Size(min=1, max=60)
    private String email;
    private boolean useractive = true;
    @Size(min=6, max=20)
    private String password;
    private boolean passwordPolicy = false;
    private boolean validEmail =true;
    private boolean isLocked = false;
    private boolean specifyPassword = false;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Public Methods
    // ---------------------------------------------------------------------------------------------

    public boolean idIsNumber() {

        return StringUtils.isNumeric(id);

    }

    public int idAsInt() {

        int ret = 0;

        if (idIsNumber()) {
            try {
                ret = Integer.parseInt(id);
            } catch (NumberFormatException ignored) { }
        }

        return ret;

    }


    // ---------------------------------------------------------------------------------------------
    // Public Methods - Accessors & Mutators (Generated)
    // ---------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = HtmlUtils.htmlEscape(firstname.trim());
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname.trim();
}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }



    public boolean isUseractive() {
        return useractive;
    }

    public void setUseractive(boolean useractive) {
        this.useractive = useractive;
    }

    public @Size(min = 1, max = 40) String getAddress1() {
        return address1;
    }

    public void setAddress1(@Size(min = 1, max = 40) String address1) {
        this.address1 = address1;
    }

    public @Size(min = 1, max = 40) String getAddress2() {
        return address2;
    }

    public void setAddress2(@Size(min = 1, max = 40) String address2) {
        this.address2 = address2;
    }

    public @Size(min = 1, max = 40) String getTown() {
        return town;
    }

    public void setTown(@Size(min = 1, max = 40) String town) {
        this.town = town;
    }

    public @Size(min = 1, max = 40) String getCountry() {
        return country;
    }

    public void setCountry(@Size(min = 1, max = 40) String country) {
        this.country = country;
    }

    public @Size(min = 1, max = 12) String getPostcode() {
        return postcode;
    }

    public void setPostcode(@Size(min = 1, max = 12) String postcode) {
        this.postcode = postcode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(boolean passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }


    public boolean isValidEmail() {
        return validEmail;
    }

    public void setValidEmail(boolean validEmail) {
        this.validEmail = validEmail;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    public boolean isSpecifyPassword() {
        return specifyPassword;
    }

    public void setSpecifyPassword(boolean specifyPassword) {
        this.specifyPassword = specifyPassword;
    }


    // ---------------------------------------------------------------------------------------------
    // Public Methods - toString
    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "UserForm{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", town='" + town + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                ", email='" + email + '\'' +
                ", useractive=" + useractive +
                ", password='" + password + '\'' +
                ", passwordPolicy=" + passwordPolicy +
                ", validEmail=" + validEmail +
                ", isLocked=" + isLocked +
                ", specifyPassword=" + specifyPassword +
                '}';
    }
}
