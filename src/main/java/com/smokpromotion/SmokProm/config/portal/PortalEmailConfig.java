package com.smokpromotion.SmokProm.config.portal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"portal","admin","dxpulse_portal","dxpulse_admin", "dentistportal"})
public class PortalEmailConfig {

    @Value("${Majorana_MAIL_FROM_ADDR:admin@imultipractice.com}")
    private  String Majorana_MAIL_FROM_ADDR;

    @Value("${Majorana_MAIL_FROM_NAME:Majorana}")
    private String Majorana_MAIL_FROM_NAME;

    @Value("${Majorana_EMAIL_LINK_ENSURE_HTTPS:false}")
    private boolean useHttps;

    public  String getMajoranaMailFromAddr() {
        return Majorana_MAIL_FROM_ADDR;
    }

    public  String getMajoranaMailFromName() {
        return Majorana_MAIL_FROM_NAME;
    }

    public boolean isUseHttps() {
        return useHttps;
    }
}
