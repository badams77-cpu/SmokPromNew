package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.config.common.BaseSecurityPrinciple;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Object held as user security principle.
 */
public class PortalSecurityPrinciple extends BaseSecurityPrinciple implements Serializable {

    private static final DateTimeFormatter TRIAL_DATE_FORMAT = DateTimeFormatter.ofPattern("eeee dd'II' MMMM yyyy");

    private static final String LITE_SUB_LEVEL = "lite";
    private static final String REGULAR_SUB_LEVEL = "regular";

    private static final String TIER1_PAT = ".*Tier\\s1.*";
    private static final String TIER2_PAT = ".*Tier\\s2.*";
    private static final String TIER3_PAT = ".*Tier\\s3.*";

    private static final long serialVersionUID = 1L;

    private boolean adminLogged = false;
    private int userLevel;
    private String userleveldesc;
    private int userTypeId;
    private String firstname;
    private String lastname;

    private LocalDate freeTrialEnd;

    private List<PortalSecurityPrincipleUserPracticeRole> userPracticeRoles = new ArrayList<>();


    private Set<String> adminAccess = new HashSet<>();

    private boolean passwordPolicy = false;
    private boolean userNeedsToSetPasswordOnLogin = false;
    private int userNeedsToSetPasswordOnLoginReason = 0;
    private String subscriptionType;
    private LocalDateTime lastLogin;
    private boolean noPII;
    private boolean schedulingEnabled;
    private boolean anyPracticeUsesUDAs = false;
    private boolean anyPracticeHasNHSCOTs = false;
    private boolean anyPracticeHasNonUDANHS = false;
    private boolean adminUser = false;
    private boolean manageUsers = false;
    private boolean subscriptionExempt = false;
    private boolean useMenuApi=false;
    private boolean lockedDown = false;
    private boolean clientAccess;
    private boolean hasDentistPortalModule;
    private String tier;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public PortalSecurityPrinciple(int id, UUID uuid, String email) {
        this.id = id;
        this.email = email;
        this.uuid = uuid;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public void setSchedulingEnabled(boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }
    
    public Set<Integer> getPracticeRoleIds() {

        if (userPracticeRoles != null && userPracticeRoles.size() > 0) {

            Set<Integer> practiceRoleIds = userPracticeRoles.stream()
                    .map(upr -> upr.getPracticeRoleId() )
                    .collect(Collectors.toSet());

            return practiceRoleIds;

        } else {

            return new HashSet<>();

        }

    }

    public boolean isAdminUser() {
        return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.adminUser = adminUser;
    }

    public  boolean isLiteUser() {
        if (subscriptionExempt){ return false; }
        return (subscriptionType != null && subscriptionType.equalsIgnoreCase(LITE_SUB_LEVEL));
    }

    public  boolean isRegularUser() {
        return (subscriptionType != null && subscriptionType.equalsIgnoreCase(REGULAR_SUB_LEVEL));
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Accessors & Mutators (Generated)
    // -----------------------------------------------------------------------------------------------------------------

    public boolean isAdminLogged() {
        return adminLogged;
    }

    public void setAdminLogged(boolean adminLogged) {
        this.adminLogged = adminLogged;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserleveldesc() {
        return userleveldesc;
    }

    public void setUserleveldesc(String userleveldesc) {
        this.userleveldesc = userleveldesc;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
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

    public Set<String> getAdminAccess() {
        return adminAccess;
    }

    public void setAdminAccess(Set<String> adminAccess) {
        this.adminAccess = adminAccess;
    }

    public boolean isPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(boolean passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
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

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDate getLastLoginDate() {
        if (lastLogin==null){
            return null;
        }
        return lastLogin.toLocalDate();
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // URC-3953 - restrict subscription exempt to client access users (which are created from admin)

    public boolean isManageUsers() {
        return manageUsers;
    }

    public void setManageUsers(boolean manageUsers) {
        this.manageUsers = manageUsers;
    }

    public boolean isUseMenuApi() {
        return useMenuApi;
    }

    public void setUseMenuApi(boolean useMenuApi) {
        this.useMenuApi = useMenuApi;
    }

    public boolean isLockedDown() {
        return lockedDown;
    }

    public void setLockedDown(boolean lockedDown) {
        this.lockedDown = lockedDown;
    }
    

    public boolean isNoPII() {
        return noPII;
    }

    public void setNoPII(boolean noPII) {
        this.noPII = noPII;
    }
    
    public LocalDate getFreeTrialEnd() {
        return freeTrialEnd;
    }

    public void setFreeTrialEnd(LocalDate freeTrialEnd) {
        this.freeTrialEnd = freeTrialEnd;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - toString
    // -----------------------------------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "PortalSecurityPrinciple{" +
                "adminLogged=" + adminLogged +
                ", userLevel=" + userLevel +
                ", userleveldesc='" + userleveldesc + '\'' +
                ", userTypeId=" + userTypeId +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", freeTrialEnd=" + freeTrialEnd +
                ", userPracticeRoles=" + userPracticeRoles +
                ", adminAccess=" + adminAccess +
                ", passwordPolicy=" + passwordPolicy +
                ", userNeedsToSetPasswordOnLogin=" + userNeedsToSetPasswordOnLogin +
                ", userNeedsToSetPasswordOnLoginReason=" + userNeedsToSetPasswordOnLoginReason +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", lastLogin=" + lastLogin +
                ", noPII=" + noPII +
                ", schedulingEnabled=" + schedulingEnabled +
                ", anyPracticeUsesUDAs=" + anyPracticeUsesUDAs +
                ", anyPracticeHasNHSCOTs=" + anyPracticeHasNHSCOTs +
                ", anyPracticeHasNonUDANHS=" + anyPracticeHasNonUDANHS +
                ", adminUser=" + adminUser +
                ", manageUsers=" + manageUsers +
                ", subscriptionExempt=" + subscriptionExempt +
                ", useMenuApi=" + useMenuApi +
                ", lockedDown=" + lockedDown +
                ", clientAccess=" + clientAccess +
                ", hasDentistPortalModule=" + hasDentistPortalModule +
                ", tier='" + tier + '\'' +
                '}';
    }

    public String toVerboseString() {

        String verbose = "[admin_logged] => " + adminLogged + "\n" +
                "[uid] => " + id + "\n" +
                "[username] => " + email + "\n" +
                "[userpw] => XXXXXX\n" +
                "[userlevel] => " + userLevel + "\n" +
                "[firstname] => " + firstname + "\n" +
                "[lastname] => " + lastname + "\n" +
                "[usertypeid] => " + userTypeId + "\n" +
                "[userleveldesc] => " + userleveldesc + "\n" +
                "[telephone] => \n" +
                "[ProfileImage] => \n" +
                "[currencySymbol] => " + currencySymbol + "\n" +
                "[adminAccess] => " + adminAccess + "\n" +
                "[anyPracticeUsesUDAs] => " + anyPracticeUsesUDAs +"\n"+
                "[anyPracticeUsesNHSCOTs] => " + anyPracticeHasNHSCOTs +"\n"+
                "[useMenuApi] => "+useMenuApi+"\n"+
                "[manageUsers] => "+manageUsers+'}';

        verbose += "[UserPractices]\n";

        for(Integer practiceId : practiceIdToName.keySet()) {

            verbose += "\t[PracticeID] => " + practiceId + "\n" +
                    "\t[PracticeName] => " + practiceIdToName.get(practiceId) + "\n" +
                    "\t[PracticeCode] => " + practiceIdToCode.get(practiceId) + "\n";

        }

        verbose += "[UserPracticeRoles]\n";

        for(PortalSecurityPrincipleUserPracticeRole role : userPracticeRoles) {

            verbose += "\t[PracticeRoleID] => " + role.getPracticeRoleId() + "\n" +
                    "\t[PracticeRoleName] => " + role.getPracticeRoleName() + "\n" +
                    "\t[PracticeID] => " + role.getPracticeId() + "\n" +
                    "\t[RoleTypeID] => " + role.getRoleTypeId() + "\n";

        }

        return verbose;

    }

}
