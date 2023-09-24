package com.smokpromotion.SmokProm.config.portal;


import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repository.REP_UserService;
import com.smokpromotion.SmokProm.util.GenericUtils;
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

import java.util.ArrayList;
import java.util.List;

@Component
@Profile(value = "portal")
public class PortalCustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalCustomAuthenticationProvider.class);

    private REP_UserService legacyMajoranaUserService;
    private PortalSecurityPrincipleService securityPrincipleService;
    private MajoranaLoginAttemptService loginAttemptService;


    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public PortalCustomAuthenticationProvider(
            REP_UserService legacyMajoranaUserService,
            PortalSecurityPrincipleService securityPrincipleService,
            MajoranaLoginAttemptService loginAttemptService
    ) {

        this.legacyMajoranaUserService = legacyMajoranaUserService;
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
            S_User u = getUserForEmail(email);

            if (u != null && u.isUseractive()) {

              //  loginAttemptService.checkIfBlockedRaiseAuthError(u);

                boolean passwordGood = legacyMajoranaUserService.isPasswordGood(u, password);

                if (passwordGood) {

                    List<String> userRoles = getRolesForUsername(u.getUsername());

                    if (userRoles != null && userRoles.size() >= 1) {

                        List<GrantedAuthority> grantedAuths = new ArrayList<>();

                        userRoles.forEach(role -> {
                            LOGGER.debug("User [" + email + "] has role [" + role + "]");
                            grantedAuths.add(new SimpleGrantedAuthority(role));
                        });

                        PortalSecurityPrinciple principle = securityPrincipleService.create(u, password);

                        if (principle != null) {

                            LOGGER.debug("User [" + email + "] successfully logged in.");
                            // System.out.println(principle.toVerboseString());

//                            if (allowGroup(principle.getPortal(), principle.getPracticeGroupId())) {
                                loginAttemptService.loginSucceeded(email);
  //                              legacyMajoranaUserService.updateHash(email, password, u, principle);
                                return new UsernamePasswordAuthenticationToken(principle, password, grantedAuths);
         //                   } else {
 //                               LOGGER.error("authenicate - Group login disallowed [" + email + "][" + principle.getPracticeGroupName() + "]");
           //                 }

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
        loginAttemptService.loginFailed(email);


        return null;

    }



    @Override
    public boolean supports(Class<?> authentication) {

        return authentication.equals(UsernamePasswordAuthenticationToken.class);

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------

    private S_User getUserForEmail(String email) {

        S_User user = legacyMajoranaUserService.getUser(email);
        user = restrictUser(user);

        return user;

    }



    public static List<String> getRolesForUsername(String email) {

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_SITE_PORTAL");

        if (email.contains("@urcompliant.com")) {
            roles.add("ROLE_URC");
        }


        roles.add("ROLE_LITE");

        return roles;

    }



    private S_User restrictUser(S_User user) {

        S_User ret = null;

        if (user != null) {
         //   if (user.isMajorana1x()) {
                ret = user;
         //   } else {
         //       LOGGER.error("User [" + user.getUsername() + "] does not have Majorana1x flag set to true.");
         //   }
        }

        return ret;

    }

}
