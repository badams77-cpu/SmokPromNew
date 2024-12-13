package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MajoranaAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public static final String ERROR_PARAM = "loginError";

    @Autowired private MajoranaLoginAttemptService loginAttemptService;
    @Autowired  private REP_UserService legacyMajoranaUserService;


    private static final Logger LOGGER = LoggerFactory.getLogger(PortalCustomAuthenticationProvider.class);


    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        String errorMessage ="";
        // The SAME error message is shown to the user in ALL cases.
        // This is so that the message itself does not indicate that it is a valid username.
        // Ensure that the message is worded such that this is the case, but that it gives sufficient detail
        // to valid users that they know how to unlock it

        errorMessage = "Incorrect/inactive username or invalid password. ";
        errorMessage += "Too many consecutive invalid attempts will lock the account. ";
        errorMessage += "Unlock using the ‘Forgot your password’ link, ";
        errorMessage += "then fully complete the steps received by email.";

        S_User possibleUser =  null;
        try {
            possibleUser = legacyMajoranaUserService.findByName(email);
        } catch (Exception e){}
        if (exception instanceof AuthenticationFailedException) {

            AuthenticationFailedException authFailedException = (AuthenticationFailedException)exception;

            if (authFailedException.getFailureReason() == AuthenticationFailureReasonEnum.BLOCKED_PWD_RECOVERY) {
                LOGGER.debug("onAuthenticationFailure - Account " + email + " Locked - Password Recovery in Progress");
            } else if (authFailedException.getFailureReason() == AuthenticationFailureReasonEnum.BLOCKED_AFTER_INVALID_ATTEMPTS) {
                LOGGER.debug("onAuthenticationFailure - Account " + email + " Locked - Too many attempts ");
            } else {
                LOGGER.debug("onAuthenticationFailure - Account " + email + " has entered bad credentials");
            }

        } else {
            LOGGER.debug("onAuthenticationFailure(...) - Account " + email + " has entered bad credential");
        }
        String referrer = request.getHeader("referer");


        request.getSession().setAttribute(ERROR_PARAM, errorMessage);
        // response.sendRedirect("/login?error="+URLEncoder.encode(errorMessage, "UTF-8"));
        response.sendRedirect("/login");
    }

}
