package com.smokpromotion.SmokProm.controller.portal;


import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Basic Controller which is called for unhandled errors
 */
//@Controller
public class AppErrorController implements ErrorController {

    private MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(ErrorController.class);
    /**
     * Error Attributes in the Application
     */
    private ErrorAttributes errorAttributes;

    private final static String ERROR_PATH = "/error";

    protected static final String PUBBASE = "/portal/public/";
    /**
     * Controller for the Error Controller
     * @param errorAttributes
     */
    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Supports the HTML Error View
     * @param request
     * @return
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHtml(Model model, ServletWebRequest request) {
        Map<String, Object> m = getErrorAttributes(request,true);
        for(String s : m.keySet()){
            LOGGER.warn(s+": "+m.get(s));
        }
        if (m.isEmpty()){
            LOGGER.warn("errorHtml no model variables");
        }
        Map<String, Object> errorMap = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        String exceptionName = (String) errorMap.get("exception");
        String exceptionMessage = (String) errorMap.get("message");
        String errorUrlTrigger = (String) errorMap.get("path");
        exceptionName = (String) errorMap.get("exception");
        exceptionMessage = (String) errorMap.get("message");
        errorUrlTrigger = (String) errorMap.get("path");
        model.addAttribute("url", errorUrlTrigger);
        model.addAttribute("email", null);
//            if (showException) {
        model.addAttribute("exceptionName", exceptionName);
        model.addAttribute("exceptionMessage", exceptionMessage);
//            }
        return PUBBASE+ERROR_PATH;
    }






    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }


    private Map<String, Object> getErrorAttributes(ServletWebRequest wr,
                                                  boolean includeStackTrace) {
        Set<ErrorAttributeOptions.Include> set = new HashSet<>();
        set.add(ErrorAttributeOptions.Include.MESSAGE);
        set.add(ErrorAttributeOptions.Include.STACK_TRACE);
        set.add(ErrorAttributeOptions.Include.EXCEPTION);
        return this.errorAttributes.getErrorAttributes(wr, ErrorAttributeOptions.of(set));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}