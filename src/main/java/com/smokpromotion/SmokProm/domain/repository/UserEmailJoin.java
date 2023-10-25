package com.smokpromotion.SmokProm.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class UserEmailJoin {

    public static String USER_TABLE = REP_UserService.USER_TABLE;

    private static String FIELDS = ", u_cr.username as created_by_useremail, "+
            "u_up.username as updated_by_useremail ";

    private static String INNER_JOIN = " INNER JOIN ";

    private static String JOIN1 = " u_cr ON en.created_by_userid = u_cr.id ";
    private static String JOIN2 = "  u_up ON en.created_by_userid = u_up.id ";


//    private static String JOIN = " INNER JOIN "+USER_TABLE + " u_cr ON en.created_by_userid = u_cr.id "+
//            " INNER JOIN "+USER_TABLE + " u_up ON en.created_by_userid = u_up.id ";


    private String userTable;

    public UserEmailJoin(REP_UserService userService){
       userTable  = userService.getTable();
    }


    public static String getFIELDS() {
        return FIELDS;
    }

    public String getJOIN() {
        return INNER_JOIN + userTable + JOIN1 +
                INNER_JOIN + userTable + JOIN2;
    }
}

