package com.smokpromotion.SmokProm.config.admin;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Object held as security principle for Admin Users.
 */
public class AdminSecurityPrinciple implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String email;
    private String firstname;
    private String lastname;
    private boolean hasDCPortal;
    private AdminUserOrgEnum organization;
    private boolean isDentrix;
    private boolean isManageAdminUsers;
    private boolean userNeedsToSetPasswordOnLogin;
    private int userNeedsToSetPasswordOnLoginReason;
    private boolean schedulingEnabled = false;
    private String language;


    private Map<Integer, List<Integer>> userAccess = new LinkedHashMap<>();

    private List<String> allowedPaths;

    private LocalDateTime lastLogin;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public AdminSecurityPrinciple(int id, String email) {
        this.id = id;
        this.email = email;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------

    public void addUserAccess(int grantTypeId, int elementId) {
        if (!userAccess.containsKey(grantTypeId)) {
            userAccess.put(grantTypeId, new ArrayList<>());
        }
        if (!userAccess.get(grantTypeId).contains(elementId)) {
            userAccess.get(grantTypeId).add(elementId);
        }
    }






    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Accessors & Mutators (Generated)
    // -----------------------------------------------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isHasDCPortal() {
        return hasDCPortal;
    }

    public void setHasDCPortal(boolean hasDCPortal) {
        this.hasDCPortal = hasDCPortal;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isDentrix() {
        return isDentrix;
    }

    public void setDentrix(boolean dentrix) {
        isDentrix = dentrix;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isManageAdminUsers() {
        return isManageAdminUsers;
    }

    public void setManageAdminUsers(boolean manageAdminUsers) {
        isManageAdminUsers = manageAdminUsers;
    }

    public List<String> getAllowedPaths() {
        return allowedPaths;
    }

    public void setAllowedPaths(List<String> allowedPaths) {
        this.allowedPaths = allowedPaths;
    }

    public boolean isAllowed(String path){
        return isManageAdminUsers || (allowedPaths!=null && allowedPaths.stream().anyMatch(url->path.equals(url ) || path.startsWith(url+"/")));
    }

    public boolean isUserNeedsToSetPasswordOnLogin() {
        return userNeedsToSetPasswordOnLogin;
    }

    public void setUserNeedsToSetPasswordOnLogin(boolean userNeedsToSetPasswordOnLogin) {
        this.userNeedsToSetPasswordOnLogin = userNeedsToSetPasswordOnLogin;
    }

    public int getUserNeedsToSetPasswordOnLoginReason() {
        return userNeedsToSetPasswordOnLoginReason;
    }

    public void setUserNeedsToSetPasswordOnLoginReason(int userNeedsToSetPasswordOnLoginReason) {
        this.userNeedsToSetPasswordOnLoginReason = userNeedsToSetPasswordOnLoginReason;
    }

    public boolean isURCompliant(){
        return email.endsWith("@urcompliant.com");
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public void setSchedulingEnabled(boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }

    public AdminUserOrgEnum getOrganization() {
        return organization;
    }

    public void setOrganization(AdminUserOrgEnum organization) {
        this.organization = organization;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - toString
    // -----------------------------------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "AdminSecurityPrinciple{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", hasDCPortal=" + hasDCPortal +
                ", organization=" + organization +
                ", isDentrix=" + isDentrix +
                ", isManageAdminUsers=" + isManageAdminUsers +
                ", userNeedsToSetPasswordOnLogin=" + userNeedsToSetPasswordOnLogin +
                ", userNeedsToSetPasswordOnLoginReason=" + userNeedsToSetPasswordOnLoginReason +
                ", schedulingEnabled=" + schedulingEnabled +
                ", language='" + language + '\'' +
                ", userAccess=" + userAccess +
                ", allowedPaths=" + allowedPaths +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
