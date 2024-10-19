package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.smokpromotion.SmokProm.domain.entity.S_User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsernameAuthProvider implements AuthenticationProvider {

    @Autowired
    private REP_UserService userService;

    @Autowired
    private PortalSecurityPrincipleService principleService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        if (!"admin".equals(name) || !"system".equals(password)) {
            return null;
        }
        S_User user;
        try {
            user = userService.findByName(name);
            if (!userService.isPasswordGood(user, password)) {
                throw new AuthenticationFailedException(AuthenticationFailureReasonEnum.UNAUTHORIZED_ACCESS,
                        "unknown user and password");
            }
            return authenticateGetAuthentication(name, password, user);
        } catch (UserNotFoundException e){
            throw new AuthenticationFailedException(AuthenticationFailureReasonEnum.UNAUTHORIZED_ACCESS,  "unknown user and password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private  UsernamePasswordAuthenticationToken authenticateGetAuthentication(String name, String password, S_User suser) {
        final List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        final UserDetails principal = new User(name, password, grantedAuths);
        PortalSecurityPrinciple principle = principleService.create(suser, password);
        return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
    }


}
