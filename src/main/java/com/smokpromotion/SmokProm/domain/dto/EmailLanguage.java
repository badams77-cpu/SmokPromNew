package com.smokpromotion.SmokProm.domain.dto;

public enum EmailLanguage {

    UNKNOWN("unknown", "Unknown",-1),
    ENGLISH("en", "English",1),
    SPANISH("es","Spanish",3),
    DUTCH("nl", "Dutch", 2);

    private final int code;
    private final String value;
    private final String label;

    private static final EmailLanguage[] NO_DUTCH = {ENGLISH, SPANISH};

     EmailLanguage(String value, String label, int code) {
        this.value = value;
        this.label = label;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public int getCode(){
         return code;
    }

    public static EmailLanguage getFromName(String language){
         for(EmailLanguage lang : values()){
             if (lang.label.equalsIgnoreCase(language)){ return lang; }
         }
         return ENGLISH;
    }

    public static EmailLanguage getFromValue(String language){
        for(EmailLanguage lang : values()){
            if (lang.value.equalsIgnoreCase(language)){ return lang; }
        }
        return ENGLISH;
    }

    public static  EmailLanguage[] valuesNoDutch(){
         return NO_DUTCH;
    }


}
