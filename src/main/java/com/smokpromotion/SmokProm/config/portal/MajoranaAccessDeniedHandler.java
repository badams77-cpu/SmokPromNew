package com.smokpromotion.SmokProm.config.portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smokpromotion.SmokProm.util.GenericUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Profile("portal")
public class MajoranaAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAccessDeniedHandler.class);

    private static final String MajoranaPAY_API = "/Majoranapay/api/";
    private static final String CASH_SHEETS_API = "/Majoranaportal/cash-sheets/api";

    private static final String APP_PERMISSION_ADMIN_API = "/application-permission-admin/api/";

    private static final String USER_PERMISSION_API = "/userpermission/api/";
    private static final String USERINFO = "/portal/api/userInformation";



        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException exc) throws IOException, ServletException {
            LOGGER.warn("handle: Access Denied Exception ",exc);
            String redirectUrl = null;
            try {
                Authentication auth
                        = SecurityContextHolder.getContext().getAuthentication();


                String action = "";
                if (request instanceof HttpServletRequest) {
                    HttpServletRequest httpRequest = (HttpServletRequest)request;
                    action = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
                };

                if ( action.startsWith(MajoranaPAY_API) || action.startsWith(CASH_SHEETS_API) || action.startsWith(USERINFO) || action.startsWith(APP_PERMISSION_ADMIN_API) || action.startsWith(USER_PERMISSION_API)) {
                    HttpServletResponse httpResponse = (HttpServletResponse)response;

                    // request has come from an API endpoint. Determine if authentication is valid, and if not, return a 403 response

                    if (auth == null || !(auth.getPrincipal() instanceof PortalSecurityPrinciple)) {
                        // authentication not valid - send 403 response
                        LOGGER.warn("handle - API call for: " + action + " but invalid security principal, returning 403 response");

                    //    AccessDeniedResponse ret = new AccessDeniedResponse();
                    //    ret.setErrorCode(BaseResponse.AUTH_FAIL_CODE);
                    //    ret.setErrorMessage(BaseResponse.AUTH_FAIL_ERROR);

                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

                        OutputStream out = httpResponse.getOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                    //    mapper.writeValue(out, ret);
                        out.flush();
                        return;
                    }
                }

                PortalSecurityPrinciple principle = (PortalSecurityPrinciple) auth.getPrincipal();

                redirectUrl = request.getContextPath() + "/accessDenied";
                String marketingPageUrl =  null; // findMarketingPageUrl(principle, request.getRequestURI());

                if (GenericUtils.isValid(marketingPageUrl)) {
                    LOGGER.warn("User: " + auth.getName()
                            + " attempted to access the APPLICATION FEATURE with path:  "
                            + request.getRequestURI()+" - Redirected to Marketing Page");




                    redirectUrl = "/marketing?path="+ marketingPageUrl;
                } else {
                    LOGGER.warn("User: " + auth.getName()
                            + " attempted to access to a PROTECTED path:  "
                            + request.getRequestURI()+" - Redirected to Access Denied");

                    redirectUrl = request.getContextPath() + "/accessDenied";
                }
            } catch (NullPointerException e) {
                redirectUrl = request.getContextPath() + "/login";
                LOGGER.debug("Session Expired. ");
                LOGGER.error(e.getMessage());
            } catch (ClassCastException e) {
                redirectUrl = request.getContextPath() + "/login";
                LOGGER.debug("Session Expired. ");
                LOGGER.error(e.getMessage());
             } catch (Exception e) {
                LOGGER.error(e.getMessage());
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }

            if (redirectUrl != null) {
                response.sendRedirect(redirectUrl);
            }
    }




}