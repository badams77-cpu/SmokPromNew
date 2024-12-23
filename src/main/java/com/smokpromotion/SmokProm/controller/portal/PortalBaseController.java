package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class PortalBaseController {

    protected static final String PRIBASE = "/portal/private/";

    protected static final String PUBBASE = "/portal/public/";

    @Autowired
    protected REP_UserService userService;

    public void setUserService(REP_UserService userService) {
        this.userService = userService;
    }

    public S_User getAuthUser(Authentication auth) throws UserNotFoundException, NotLoggedInException {
        Object prince = auth.getPrincipal();
        if (prince==null || !(prince instanceof PortalSecurityPrinciple)) throw new NotLoggedInException("Please login first");
        PortalSecurityPrinciple principle = (PortalSecurityPrinciple) prince;
        return userService.findByName(((PortalSecurityPrinciple) prince).getEmail());
    }

    public S_User getAdminUser(String s) throws UserNotFoundException{
        return userService.findByName(s);
    }

}
