package com.smokpromotion.SmokProm.controller;

import com.smokpromotion.SmokProm.Exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

public class BaseLogedInController {

    protected PortalSecurityPrinciple getPrincipal(Authentication auth){
        PortalSecurityPrinciple prince = (PortalSecurityPrinciple)  auth.getPrincipal();
        return prince;
    }

//    private Dbbean dbBean;

    protected S_User getUser(PortalSecurityPrinciple p) throws UserNotFoundException {

        return null;
    }

    protected String handleUserNotFoundException(UserNotFoundException  ex, Model m){
        m.addAttribute("Error", "User "+ex.getUserName()+" Not found");
    }


}
