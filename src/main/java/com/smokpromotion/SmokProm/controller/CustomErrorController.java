package com.smokpromotion.SmokProm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Profile("not_used")
@Controller
public class CustomErrorController extends AbstractErrorController {


    private @Value("${MPC_SHOW_EXCEPTION:true}") boolean showException;
    private static final String ERROR_PATH = "/error";
    @Autowired private ErrorAttributes errorAttributes;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorController.class);
    private final String  methodClass = "ErrorController.error(...) - ";

    public CustomErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(ERROR_PATH)
    public String error(WebRequest request, Model model){
        Map<String, Object> errorMap = null;
        String exceptionName = "";
        String exceptionMessage ="";
        String errorUrlTrigger = "";
        String referer = request.getHeader("Referer");
        model.addAttribute("referer",referer);
        try {
            errorMap = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
            exceptionName = (String) errorMap.get("exception");
            exceptionMessage = (String) errorMap.get("message");
            errorUrlTrigger = (String) errorMap.get("path");
            model.addAttribute("url", errorUrlTrigger);
            model.addAttribute("email", null);
            model.addAttribute("showException",showException);
            if (showException) {
                model.addAttribute("exceptionName", exceptionName);
                model.addAttribute("exceptionMessage", exceptionMessage);
            }
        } catch (Exception e ) {
            LOGGER.error(methodClass+"An error occurred INSIDE the ErrorController.");
            LOGGER.error(methodClass+"Probably an expired session triggered the Exception.");
            LOGGER.error(methodClass+e.getMessage());
        } finally {
            LOGGER.error(String.format(methodClass+"handling error: %s raised from %s",exceptionName, errorUrlTrigger));
            LOGGER.error(methodClass+exceptionMessage);
        }

        return "portal/public/error";
    }

}