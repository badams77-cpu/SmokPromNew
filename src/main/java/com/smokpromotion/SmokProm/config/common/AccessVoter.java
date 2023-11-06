package com.smokpromotion.SmokProm.config.common;

import com.smokpromotion.SmokProm.config.portal.BaseAccessDecisionManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

public class AccessVoteri implements AccessDecisionVoter {

    private BaseAccessDecisionManager  baseAccessDecisionManager;

    public AccessVoteri(BaseAccessDecisionManager baseAccessDecisionManager){
        this.baseAccessDecisionManager = baseAccessDecisionManager;
    }



    @Override
    public int vote(
            Authentication authentication, Object object, Collection collection) {
        HttpServletRequest req = (HttpServletRequest) object;
        String path = req.getContextPath();
        return authentication.getAuthorities().stream()
      //          .map(GrantedAuthority::getAuthority)
      //          .filter(r -> "ROLE_USER".equals(r))
       //                 && LocalDateTime.now().getMinute() % 2 != 0)
      //          .findAny()
                .map( a -> baseAccessDecisionManager.allowSection(req, authentication, path)
                    ? AccessDecisionVoter.ACCESS_GRANTED : AccessDecisionVoter.ACCESS_DENIED
                ).findFirst()
      //          .map(s -> AccessDecisionVoter.ACCESS_DENIED)
                .orElseGet(() -> ACCESS_ABSTAIN);
    }


    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class clazz) {
        return false;
    }
}
