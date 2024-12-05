package com.smokpromotion.SmokProm.controller.portal;


import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private ErrorAttributes errorAttributes;

    private static final String INCOMING_REQUEST_FAILED = "Incoming request failed:";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);


    /**
     * Controller for the Error Controller
     * @param errorAttributes
     */
    @Autowired
    public ExceptionControllerAdvice(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView  internalServerError(ServletWebRequest request,final Exception ex) {
        Map<String, Object> m = getErrorAttributes(request,true);
        for(String s : m.keySet()){
            LOGGER.warn(s+": "+m.get(s));
        }
        if (m.isEmpty()){
            LOGGER.warn("internalServerError no model variables");
        }
        return new ModelAndView("/error", m);
    }

    private Map<String, Object> getErrorAttributes(ServletWebRequest wr,
                                                   boolean includeStackTrace) {
        Set<ErrorAttributeOptions.Include> set = new HashSet<>();
        set.add(ErrorAttributeOptions.Include.MESSAGE);
        set.add(ErrorAttributeOptions.Include.STACK_TRACE);
        set.add(ErrorAttributeOptions.Include.EXCEPTION);
        return this.errorAttributes.getErrorAttributes(wr, ErrorAttributeOptions.of(set));
    }
}