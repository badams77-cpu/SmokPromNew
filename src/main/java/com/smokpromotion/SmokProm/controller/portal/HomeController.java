package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Profile("smok_app")
@Controller
public class HomeController extends PortalBaseController {

    @RequestMapping("/a/home")
    public String home(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        return PRIBASE+"home.html";
    }


    @RequestMapping("/login")
    public String login(Model m, Authentication auth)
    {
        return PUBBASE+"login.html";
    }
}
