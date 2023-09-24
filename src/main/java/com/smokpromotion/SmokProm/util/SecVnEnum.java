package com.smokpromotion.SmokProm.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum SecVnEnum {

    UNKNOWN(-1, "unknown"),
    MD5(0,"md5"),
    BCRYPT( 1, "bcrypt");

    public static int MD5Num = 0;

    public static int BCryptNum = 1;

    private final int code;

    private final String description;



    SecVnEnum(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Retrieve the LanguageSettingEnum value from the given code.
     * @param code - the code to match on
     * @return The relevant LanguageSettingEnum value for code, or null if none found
     */
    public static SecVnEnum getFromCode(int code) {
        return getFromFilter(a -> a.getCode()==code);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private methods
    // -----------------------------------------------------------------------------------------------------------------

    private static SecVnEnum getFromFilter(Predicate<SecVnEnum> filterFunc) {
        Optional<SecVnEnum> found = Arrays.stream(SecVnEnum.values()).filter(a -> filterFunc.test(a)).findFirst();
        return found.orElse(UNKNOWN);
    }


}
