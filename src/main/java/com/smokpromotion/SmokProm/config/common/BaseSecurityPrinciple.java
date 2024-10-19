package com.smokpromotion.SmokProm.config.common;


import com.majorana.maj_orm.DBs.MajDataSourceName;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseSecurityPrinciple implements Serializable {

    private static final long serialVersionUID = 0xbeef800080008001L;


    protected int id;
    protected UUID uuid;
    protected String email;
    protected String firstname;
    protected String lastname;
    protected String userLanguage;

    protected MajDataSourceName dbSrcName;

    protected String currencySymbol;

    protected Map<Integer, String> practiceIdToName = new LinkedHashMap<>();
    protected Map<Integer, String> practiceIdToCode = new LinkedHashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MajDataSourceName getDbSrcName() {
        return dbSrcName;
    }

    public void setDbSrcName(MajDataSourceName dbSrcName) {
        this.dbSrcName = dbSrcName;
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

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public Map<Integer, String> getPracticeIdToName() {
        return practiceIdToName;
    }

    public void setPracticeIdToName(Map<Integer, String> practiceIdToName) {
        this.practiceIdToName = practiceIdToName;
    }

    public Map<Integer, String> getPracticeIdToCode() {
        return practiceIdToCode;
    }

    public void setPracticeIdToCode(Map<Integer, String> practiceIdToCode) {
        this.practiceIdToCode = practiceIdToCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getPracticeName(int practiceId) {
        return practiceIdToName.get(practiceId);
    }

    public String getPracticeCode(int practiceId) {
        return practiceIdToCode.get(practiceId);
    }

    public List<Integer> getPracticeIds() {
        return new ArrayList(getPracticeIdToName().keySet());
    }

    public List<Integer> getPracticeIds(List<Integer> practiceIdList) {
        return practiceIdList.stream().filter(id -> this.practiceIdToCode.containsKey(id)).collect(Collectors.toList());
    }

    public Set<String> getSetOfPracticeCodes() {
        return new HashSet(practiceIdToCode.values());
    }

    public List<String> getPracticeCodes() {
        List<String> practiceCodesList = null;
        if (getSetOfPracticeCodes() != null && !getSetOfPracticeCodes().isEmpty()) {
            practiceCodesList = new ArrayList<String>(getSetOfPracticeCodes());
        }
        return practiceCodesList;
    }

    public List<String> getPracticeCodes(Set<Integer> practiceIdSet) {
        return getPracticeCodes(practiceIdSet.stream().collect(Collectors.toList()));
    }

    public List<String> getPracticeCodes(List<Integer> practiceIdList) {
        List<String> practiceCodes = new ArrayList<>();
        for(Integer practiceId : practiceIdList) {
            String code = getPracticeCode(practiceId);
            if (code != null) {
                practiceCodes.add(code);
            }
        }
        return practiceCodes;
    }



    @Override
    public String toString() {
        return "BaseSecurityPrinciple{" +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", userLanguage=" + userLanguage +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", practiceIdToName=" + practiceIdToName +
                ", practiceIdToCode=" + practiceIdToCode +
                '}';
    }
}
