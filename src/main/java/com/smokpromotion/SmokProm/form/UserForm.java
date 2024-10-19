package com.smokpromotion.SmokProm.form;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class backing the "Your Profile" form offered to end users.
 */
public class UserForm {

    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String organization;
    private String language;
    private boolean useractive = true;
    private int practiceGroupId;
    private String password;
    private boolean passwordPolicy = false;
    private boolean validEmail =true;
    private boolean isLocked = false;
    private boolean manageAdminRoles = false;
    private boolean subscriptionExempt = false;
    private boolean manageUsers = false;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language==null ? null :  language.trim();
    }

    public boolean isUseractive() {
        return useractive;
    }

    public void setUseractive(boolean useractive) {
        this.useractive = useractive;
    }



    public int getPracticeGroupId() {
        return practiceGroupId;
    }

    public void setPracticeGroupId(int practiceGroupId) {
        this.practiceGroupId = practiceGroupId;
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

    public boolean isManageAdminRoles() {
        return manageAdminRoles;
    }

    public void setManageAdminRoles(boolean manageAdminRoles) {
        this.manageAdminRoles = manageAdminRoles;
    }

    public boolean isSubscriptionExempt() {
        return subscriptionExempt;
    }

    public void setSubscriptionExempt(boolean subscriptionExempt) {
        this.subscriptionExempt = subscriptionExempt;
    }

    public boolean isManageUsers() {
        return manageUsers;
    }

    public void setManageUsers(boolean manageUsers) {
        this.manageUsers = manageUsers;
    }

    public boolean isSpecifyPassword() {
        return specifyPassword;
    }

    public void setSpecifyPassword(boolean specifyPassword) {
        this.specifyPassword = specifyPassword;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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
                ", email='" + email + '\'' +
                ", org='" + organization + '\'' +
                ", language='" + language + '\'' +
                ", useractive=" + useractive +
                ", practiceGroupId=" + practiceGroupId +
                ", password='" + password + '\'' +
                ", passwordPolicy=" + passwordPolicy +
                ", validEmail=" + validEmail +
                ", isLocked=" + isLocked +
                ", manageAdminRoles=" + manageAdminRoles +
                ", subscriptionExempt=" + subscriptionExempt +
                ", manageUsers=" + manageUsers +
                ", specifyPassword=" + specifyPassword +
                '}';
    }

}
