package com.smokpromotion.SmokProm.controller.portal.identity;

import com.electronwill.nightconfig.core.conversion.Path;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegisterController {

    @RequestMapping(value="/register")
    public String getRegForm(){
        return "portal/public/register";
    }

}

