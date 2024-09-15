package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class BaseAccessDecisionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAccessDecisionManager.class);



    public boolean allowSection(ServerHttpRequest request, Authentication authentication, String basePath) {
        boolean ret = false;
        if (GenericUtils.isValid(basePath) && GenericUtils.isValid(basePath)) {
            basePath=  cleanPathFromPost(basePath);
            basePath = "/"+basePath;
        }
        return checkPath(request, authentication,basePath);
    }

    public boolean allowSection(ServerHttpRequest request, Authentication authentication, String basePath, String path) {
        boolean ret = false;
        if (GenericUtils.isValid(path) && GenericUtils.isValid(basePath)) {
            path=  cleanPathFromPost(path);
            path = "/"+basePath+"/"+path;
        }
        return checkPath(request, authentication,path);
    }

    public boolean allowSection(ServerHttpRequest request, Authentication authentication, String basePath, String path, String innerPath) {
        boolean ret = false;
        if (GenericUtils.isValid(path) && GenericUtils.isValid(basePath) && GenericUtils.isValid(innerPath)) {
            path=  cleanPathFromPost(path);
            path = "/"+basePath+"/"+path+"/"+innerPath;
        }
        return checkPath(request, authentication,path);
    }

    private String cleanPathFromPost(String path) {
       path = path.replace("-post", "");
       return path;
    }

    protected boolean checkPath(ServerHttpRequest request, Authentication authentication, String path) {
        boolean ret = false;
        try {

//            if (path != null && path.equalsIgnoreCase("/recovery-password")) {
//                return true;
//            } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            PortalSecurityPrinciple principle = (PortalSecurityPrinciple) auth.getPrincipal();

            ret = checkAccessForPath(request,
                    // principle,
                    authentication, path);

            LOGGER.info("checkPath - userid "+principle.getId()+" "+(ret?" allowed ":" denied ")+"access to "+path );
//            }
        } catch(ClassCastException e){
            LOGGER.debug("checkPath invalid principal ");
            LOGGER.error(e.getMessage());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getPrincipal() instanceof String){
                LOGGER.debug("Principal was a String:"+ (String) auth.getPrincipal());
            }
        } catch(Exception e){
            LOGGER.error(e.getMessage());
        }


        return ret;
    }

    protected abstract boolean checkAccessForPath(ServerHttpRequest request,
                                                  // PortalSecurityPrinciple principle,
                                                  Authentication authentication,
                                                  String path) throws Exception ;


}

