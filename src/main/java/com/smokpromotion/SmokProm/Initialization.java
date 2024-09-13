package com.smokpromotion.SmokProm;

import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_AdminUserService;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class Initialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    private REP_UserService userService;

    @Autowired
    private REP_AdminUserService adminUserService;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private PwCryptUtil pwCrypt;
    private final static String USERNAME = "barry.david.adams@gmail.com";

    private final static String FIRST = "Barry";

    private final static String LAST = "Adams";

    private final static String FIRSTPASS = "changeme";



    public void init() throws SQLException  {
        try {
            S_User user = userService.findByName(USERNAME);
        } catch (UserNotFoundException e){

        //if (user==null){
            S_User user = new S_User();
           user.setFirstname(FIRST);
            user.setLastname(LAST);
            user.setUsername(USERNAME);
            user.setSecVn(1);
            userService.create(user, FIRSTPASS);
            int id = user.getId();
            if (id!=0){
                LOGGER.warn("User created "+USERNAME+ "id="+id);
            } else {
                LOGGER.warn("User creation failed");
            }
        }
        try {
        AdminUser auser = adminUserService.findByName(USERNAME);
        } catch (UserNotFoundException e){
            AdminUser auser = new AdminUser();
            auser.setFirstname(FIRST);
            auser.setLastname(LAST);
            auser.setUsername(USERNAME);
            auser.setSecVn(1);
            adminUserService.create(auser, FIRSTPASS);
            int id = auser.getId();
            if (id!=0){
                LOGGER.warn("AdminUser created "+USERNAME+ "id="+id);
            } else {
                LOGGER.warn("AdminUser creation failed");
            }
        }

    }

}
