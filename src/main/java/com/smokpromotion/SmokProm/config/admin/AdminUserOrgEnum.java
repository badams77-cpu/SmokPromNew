package com.smokpromotion.SmokProm.config.admin;

public enum AdminUserOrgEnum {

    UNKNOWN("",0,"",false),
    URC("URC",0,"",false),
    SOE("SOE",0,"",false),
    MEDIHOLDING("MEDIHOLDINGS",99,"PortalMediholdings",true);

    private String code;
    private String portalRoleGroup;
    private int maxPractices;
    private boolean noPII;

    AdminUserOrgEnum(String code, int maxPractices, String portalRoleGroup, boolean noPII){
        this.code = code;
        this.maxPractices = maxPractices;
        this.portalRoleGroup = portalRoleGroup;
        this.noPII = noPII;
    }

    public String getPortalRoleGroup() {
        return portalRoleGroup;
    }

    public void setPortalRoleGroup(String portalRoleGroup) {
        this.portalRoleGroup = portalRoleGroup;
    }

    public int getMaxPractices() {
        return maxPractices;
    }

    public void setMaxPractices(int maxPractices) {
        this.maxPractices = maxPractices;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isNoPII() {
        return noPII;
    }

    public void setNoPII(boolean noPII) {
        this.noPII = noPII;
    }

    public static AdminUserOrgEnum getByName(String code){
        for(AdminUserOrgEnum org : values()){
            if (org.name().equals(code)){ return org; }
        }
        return UNKNOWN;
    }

    public static AdminUserOrgEnum getByCode(String code){
        for(AdminUserOrgEnum org : values()){
            if (org.code.equals(code)){ return org; }
        }
        return UNKNOWN;
    }
}
