package com.smokpromotion.SmokProm.analytics;



import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represent the site the analytis are for
 */
public enum AnalyticsSiteEnum {

    PORTAL("portal","portal"),
    ADMIN("admin","admin"),
    DENTIST_PORTAL("dentist_portal","dentistportal")
    ;

    private String code;
    private String alt;

    AnalyticsSiteEnum( String code, String alt) {

        this.code = code;
        this.alt = alt;
    }

    public String getCode() { return this.code; }

    public String getAlt(){ return alt; }

    // Public Static Methods
    // -----------------------------------------------------------------------------------------------------------------

    public static List<AnalyticsSiteEnum> GetAll() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }


    /**
     * Retrieve the AnalyticsSiteEnum value from the given code.
     * @param code - the code to match on
     */
    public static AnalyticsSiteEnum GetFromCode(String code) {
        if (code == null) {
            return null;
        }
        return GetFromFilter(a -> a.getCode().equals(code) || a.getAlt().equals(code));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private methods
    // -----------------------------------------------------------------------------------------------------------------

    private static AnalyticsSiteEnum GetFromFilter(Predicate<AnalyticsSiteEnum> filterFunc) {
        Optional<AnalyticsSiteEnum> found = Arrays.stream(values()).filter(a -> filterFunc.test(a) ).findFirst();
        return (found.isPresent()) ? found.get() : null;
    }

}
