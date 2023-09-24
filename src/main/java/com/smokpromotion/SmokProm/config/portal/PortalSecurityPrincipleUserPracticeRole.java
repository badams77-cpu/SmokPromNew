package com.smokpromotion.SmokProm.config.portal;

import java.io.Serializable;

public class PortalSecurityPrincipleUserPracticeRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private int practiceRoleId;
    private String practiceRoleName;
    private int practiceId;
    private int roleTypeId;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public PortalSecurityPrincipleUserPracticeRole(int practiceRoleId, String practiceRoleName, int practiceId, int roleTypeId) {
        this.practiceRoleId = practiceRoleId;
        this.practiceRoleName = practiceRoleName;
        this.practiceId = practiceId;
        this.roleTypeId = roleTypeId;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Accessors & Mutators (Generated)
    // -----------------------------------------------------------------------------------------------------------------

    public int getPracticeRoleId() {
        return practiceRoleId;
    }

    public void setPracticeRoleId(int practiceRoleId) {
        this.practiceRoleId = practiceRoleId;
    }

    public String getPracticeRoleName() {
        return practiceRoleName;
    }

    public void setPracticeRoleName(String practiceRoleName) {
        this.practiceRoleName = practiceRoleName;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public int getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(int roleTypeId) {
        this.roleTypeId = roleTypeId;
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - toString
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "PortalSecurityPrincipleUserPractice{" +
                "practiceRoleId=" + practiceRoleId +
                ", practiceRoleName='" + practiceRoleName + '\'' +
                ", practiceId=" + practiceId +
                ", roleTypeId=" + roleTypeId +
                '}';
    }
}
