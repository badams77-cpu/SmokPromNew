package com.smokpromotion.SmokProm.config.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private static final String ADMINPORTAL_REQUEST_PARAM = "ap";

    private final HttpStatusReturningLogoutSuccessHandler successStatusReturningHandler;

    public AdminLogoutSuccessHandler() {
        this.successStatusReturningHandler = new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
    }

//    @Override
    public void onLogoutSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {

        if (request.getParameter(ADMINPORTAL_REQUEST_PARAM) != null) {
            // logout initiated from the admin portal logout endpoint
      //      successStatusReturningHandler.onLogoutSuccess(request, response, authentication);
        } else {
            // default behaviour
      //      super.onLogoutSuccess(request, response, authentication);
        }
    }
}
