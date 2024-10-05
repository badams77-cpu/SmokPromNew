package com.smokpromotion.SmokProm.controller.admin;

import com.smokpromotion.SmokProm.config.admin.AdminSecurityPrinciple;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.repo.REP_AdminUserService;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class AdminBaseController {

    protected static final String PRIBASE = "/admin/private/";

    protected static final String PUBBASE = "/admin/public/";


    @Autowired
    REP_AdminUserService userService;


    public AdminUser getAuthUser(Authentication auth) throws UserNotFoundException, NotLoggedInException {
        Object prince = auth.getPrincipal();
        if (prince==null || !(prince instanceof AdminSecurityPrinciple)) throw new NotLoggedInException("Please login first");
        AdminSecurityPrinciple principle = (AdminSecurityPrinciple) prince;
        return userService.findByName(((AdminSecurityPrinciple) prince).getEmail());
    }

    public void checkAuthentication(Authentication auth) throws Exception {
        getAuthUser(auth);
    }

    public String getBase(){
        return PRIBASE;
    }

}
