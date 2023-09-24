package com.smokpromotion.SmokProm.config.portal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class MajoranaPayCustomAPISecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

//    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaPayCustomAPISecurityFilter.class);
//
//    // Regexp that represents URLs that are considered as API calls from the frontend
//    private static final String MajoranaPAY_API = "/Majoranapay/api/";
//    private static final String CASH_SHEETS_API = "/Majoranaportal/cash-sheets/api";
//    private static final String APP_PERMISSION_ADMIN_API = "/application-permission-admin/api/";
//    private static final String USER_PERMISSION_API = "/userpermission/api/";
//
//    private static final String USERINFO = "/portal/api/userInformation";
//
//    public static String GetMajoranaPayPortalApiUrlPattern() {
//        return MajoranaPAY_API;
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        try {
//            String action = "";
//            if (request instanceof HttpServletRequest) {
//                HttpServletRequest httpRequest = (HttpServletRequest)request;
//                action = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
//            };
//
//            if ( action.startsWith(MajoranaPAY_API) || action.startsWith(CASH_SHEETS_API) || action.startsWith(USERINFO) || action.startsWith(APP_PERMISSION_ADMIN_API) || action.startsWith(USER_PERMISSION_API)) {
//                HttpServletResponse httpResponse = (HttpServletResponse)response;
//
//                // request has come from an API endpoint. Determine if authentication is valid, and if not, return a 403 response
//                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//                if (auth == null || !(auth.getPrincipal() instanceof PortalSecurityPrinciple)) {
//                    // authentication not valid - send 403 response
//                    LOGGER.warn("doFilter - API call for: " + action + " but invalid security principal, returning 403 response");
//
//                    AccessDeniedResponse ret = new AccessDeniedResponse();
//                    ret.setErrorCode(BaseResponse.AUTH_FAIL_CODE);
//                    ret.setErrorMessage(BaseResponse.AUTH_FAIL_ERROR);
//
//                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//                    OutputStream out = httpResponse.getOutputStream();
//                    ObjectMapper mapper = new ObjectMapper();
//                    mapper.writeValue(out, ret);
//                    out.flush();
//                    return;
//                }
//            }
////        }
//        catch (Exception ex) {
//            LOGGER.error("doFilter - error occurred in custom handling, defaulting to remaining filter chain");
//        }
//
//        chain.doFilter(request, response);
//    }

}
