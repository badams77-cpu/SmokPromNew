package com.smokpromotion.SmokProm.controller.admin;

import com.smokpromotion.SmokProm.controller.portal.PortalBaseController;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminHomeController extends PortalBaseController {

    @GetMapping("/home")
    public String home(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        return PRIBASE+"home.html";
    }
}
