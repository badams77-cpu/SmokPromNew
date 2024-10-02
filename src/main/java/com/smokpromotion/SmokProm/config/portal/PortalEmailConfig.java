package com.smokpromotion.SmokProm.config.portal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"portal","admin","dxpulse_portal","dxpulse_admin", "dentistportal"})
public class PortalEmailConfig {

    @Value("${MPC_MAIL_FROM_ADDR:admin@imultipractice.com}")
    private  String MPC_MAIL_FROM_ADDR;

    @Value("${MPC_MAIL_FROM_NAME:MPC}")
    private String MPC_MAIL_FROM_NAME;

    @Value("${MPC_EMAIL_LINK_ENSURE_HTTPS:false}")
    private boolean useHttps;

    public  String getMpcMailFromAddr() {
        return MPC_MAIL_FROM_ADDR;
    }

    public  String getMpcMailFromName() {
        return MPC_MAIL_FROM_NAME;
    }

    public boolean isUseHttps() {
        return useHttps;
    }
}
