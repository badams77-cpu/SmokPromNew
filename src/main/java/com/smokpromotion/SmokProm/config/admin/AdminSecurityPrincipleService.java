package com.smokpromotion.SmokProm.config.admin;

import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Profile(value = {"admin", "dxpulse_admin"})
@Service
public class AdminSecurityPrincipleService {

    private static final String ADMIN_HASH = "$2a$10$Dro7gKfxS7qyGBHiel8d4u8eJKZD9g6IzVZnbDNduSY4Rd91E86oO";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSecurityPrincipleService.class);


    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------


    @Autowired
    public AdminSecurityPrincipleService(
        @Value("${Majorana_ADMIN_PASSWORD:xxx}")    String adminPass
    ) {
        BCryptPasswordEncoder  crypt = new BCryptPasswordEncoder();
        if (!crypt.matches(adminPass, ADMIN_HASH)) {
            throw new IllegalArgumentException("Majorana_ADMIN_PASSWORD is incorrect");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------


    public AdminSecurityPrinciple create(AdminUser legacyUser, String password) {

        AdminSecurityPrinciple authUser = new AdminSecurityPrinciple(legacyUser.getId(), legacyUser.getUsername());

        authUser.setFirstname(legacyUser.getFirstname());
        authUser.setLastname(legacyUser.getLastname());
        authUser.setLastLogin(legacyUser.getLastvisit());

        // Determine admin access permissions (e.g., "users").
        return authUser;

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------


}
