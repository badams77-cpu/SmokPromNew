package com.smokpromotion.SmokProm.controller.portal;

import com.electronwill.nightconfig.core.conversion.Path;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("smok-app")
@Controller
public class IndexPageController {

        @RequestMapping(value="/index")
        public String getRegForm() {
            return "portal/public/index";
        }


}
