package com.smokpromotion.SmokProm.controller.portal.identity;

import com.electronwill.nightconfig.core.conversion.Path;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("smok-app")
@Controller
public class RegisterController {

    @RequestMapping(value="/register")
    public String getRegForm(){
        return "portal/public/register";
    }

}

