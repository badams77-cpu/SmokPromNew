package com.smokpromotion.SmokProm.controller;

import com.smokpromotion.SmokProm.Exceptions.UserNotFoundException;
//import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class MainController extends BaseLogedInController {

    private static final String PATH_PREFIX = "portal/private/";


    @RequestMapping(path = "/public/index.html")
    public String index(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();
        try {
            PortalSecurityPrinciple princ = getPrincipal(auth);
            S_User user;
            user = getUser(princ);
        } catch (Exception ex) {

            if (ex instanceof UserNotFoundException) {
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                UserNotFoundException unfe = (UserNotFoundException) ex;

                return handleUserNotFoundException(unfe, m);
            }
            //     } else if (ex instanceof ContentNotAllowedException) {
            //         HttpStatus status = HttpStatus.BAD_REQUEST;
            //         ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

            String baldy = " Internal Server Exception ";

//        setModel(m, ex);

            return PATH_PREFIX + "/home.html";
        }
        //    } else {
        //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        //        return handleExceptionInternal(ex, null, headers, status, request);
        //    }
        return PATH_PREFIX + "/unexpected";
    }


    @RequestMapping(path = "/signedin/home")
    public String home(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();
        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException) {

//        setModel(m, ex);

                return "/portal/private/home.html";
                //    } else {
                //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                //        return handleExceptionInternal(ex, null, headers, status, request);
                //    }
            }

        }

        return PATH_PREFIX+"home";
    }

}

