package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.util.GenericUtils;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile(value = {"admin", "dxpulse_admin"})
public class AdminCustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCustomAuthenticationProvider.class);
    private REP_AdminUserService adminMajoranaUserService;
    private AdminSecurityPrincipleService securityPrincipleService;
    private AdminMajoranaLoginAttemptService loginAttemptService;

    private PwCryptUtil pwCrypt;


    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public AdminCustomAuthenticationProvider(
            PwCryptUtil pwCrypt,
            REP_AdminUserService adminMajoranaUserService,
            AdminSecurityPrincipleService securityPrincipleService,
            AdminMajoranaLoginAttemptService loginAttemptService
    ) {
        this.pwCrypt = pwCrypt;
        this.adminMajoranaUserService = adminMajoranaUserService;
        this.securityPrincipleService = securityPrincipleService;
        this.loginAttemptService = loginAttemptService;

    }
    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Override AuthenticationProvider
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();


        if (GenericUtils.isValid(email) && GenericUtils.isValid(password) ) {
            AdminUser u = adminMajoranaUserService.getUser(email);

//            loginAttemptService.checkIfBlockedRaiseAuthError(p);

            if (u != null ) {

                boolean passwordGood = adminMajoranaUserService.isPasswordGood(u, password);

                if (passwordGood) {

                    List<String> userRoles = getRolesForUsername(u.getUsername());

                    if (userRoles != null && userRoles.size() >= 1) {

                        List<GrantedAuthority> grantedAuths = new ArrayList<>();

                        userRoles.forEach(role -> {
                            LOGGER.debug("User [" + email + "] has role [" + role + "]");
                            grantedAuths.add(new SimpleGrantedAuthority(role));
                        });

                        AdminSecurityPrinciple principle = securityPrincipleService.create(u,password);
                        if (principle!=null) {
                            LOGGER.debug("User [" + email + "] successfully logged in.");
//                            loginAttemptService.loginSucceeded(u);
//                            adminMajoranaUserService.updateHash(email, password, u);
                            u.setUserpw( pwCrypt.getPasswd(password, u.getSecVn()));
                            u.setLastvisit(LocalDateTime.now());
                            adminMajoranaUserService.updateUser(u);
                            return new UsernamePasswordAuthenticationToken(principle, password, grantedAuths);
                        } else {
                            LOGGER.error("authenticate - Attempted login [" + email + "] - No Principole.");
                        }

                    } else {

                        LOGGER.error("authenticate - Attempted login [" + email + "] - User has no roles.");

                    }

                } else {
                    LOGGER.error("authenticate - Attempted login [" + email + "] - Incorrect password provided.");

                }

            } else {

                LOGGER.error("authenticate - Attempted login [" + email + "] - User not found.");

            }
        } else {

            LOGGER.error("authenticate - Attempted login email=[" + email + "] -  empty inputs");

        }
//        loginAttemptService.loginFailed(email);


        return null;

    }

    @Override
    public boolean supports(Class<?> authentication) {

        return authentication.equals(UsernamePasswordAuthenticationToken.class);

    }

    public static List<String> getRolesForUsername(String username) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_SITE_ADMIN");
        return roles;

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------

    private AdminUser getUserForEmail(String email) {
        return adminMajoranaUserService.getUser(email);
    }



}
