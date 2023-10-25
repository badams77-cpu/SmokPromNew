package com.smokpromotion.SmokProm.domain.repository;

public class UserEmailJoin {

    public static String USER_TABLE = REP_UserService.USER_TABLE;

    private static String FIELDS = ", u_cr.username as created_by_useremail, "+
            "u_up.username as updated_by_useremail ";
    private static String JOIN = " INNER JOIN "+USER_TABLE + " u_cr ON en.created_by_userid = u_cr.id "+
            " INNER JOIN "+USER_TABLE + " u_up ON en.created_by_userid = u_up.id ";

    public static String getFIELDS() {
        return FIELDS;
    }

    public static String getJOIN() {
        return JOIN;
    }
}

