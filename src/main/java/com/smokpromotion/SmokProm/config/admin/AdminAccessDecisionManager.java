package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.config.common.SubscriptionAccessManager;
import com.smokpromotion.SmokProm.config.portal.BaseAccessDecisionManager;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class AdminAccessDecisionManager extends BaseAccessDecisionManager  {

    @Autowired private SubscriptionAccessManager subscriptionAccess;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAccessDecisionManager.class);

    @Override
    protected boolean checkPath(ServerHttpRequest request, Authentication authentication, String path) {
        boolean ret = false;
        try {

//            if (path != null && path.equalsIgnoreCase("/recovery-password")) {
//                return true;
//            } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AdminSecurityPrinciple principle = (AdminSecurityPrinciple) auth.getPrincipal();

            ret = checkAccessForPath(request, principle, authentication, path);

            LOGGER.info("checkPath: - "+(ret?"allowed":" denied ")+"access to "+path );
//            }
        } catch(ClassCastException e){
            LOGGER.debug("checkPath: invalid principal path was "+path);
            LOGGER.error(e.getMessage());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getPrincipal() instanceof String){
                LOGGER.debug("checkPath: Principal was a String:"+ (String) auth.getPrincipal());
            }
        } catch(BadSqlGrammarException e){
            LOGGER.error("checkPath: DB error, please check roles rolegroup and application feature Schema",e);
            ret = true;

            LOGGER.error("checkPath: "+e.getMessage());
        } catch(Exception e){
            LOGGER.error("checkPath ",e);
        }


        return ret;
    }


    protected boolean checkAccessForPath(ServerHttpRequest request, AdminSecurityPrinciple principle, Authentication authentication, String path) throws Exception {
        return subscriptionAccess.checkAdminAccess(path, principle);
    }

    protected boolean checkAccessForPath(ServerHttpRequest request, PortalSecurityPrinciple principle, Authentication authentication, String path) throws Exception {
        return false; // No Access with Portal Principle
    }
}

