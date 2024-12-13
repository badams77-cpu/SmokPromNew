package com.smokpromotion.SmokProm.config.portal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"smok_app","smok_admin","smok_init","init"})
public class PortalEmailConfig {

    @Value("${VAPID_MAIL_FROM_ADDR:admin@vapidpromotions.com}")
    private  String MPC_MAIL_FROM_ADDR;

    @Value("${VAPID_MAIL_FROM_NAME:admin}")
    private String MPC_MAIL_FROM_NAME;

    @Value("${VAPID_EMAIL_LINK_ENSURE_HTTPS:true}")
    private boolean useHttps;

    @Value("${VAPID_EMAIL_LINK_DEFAULT_CONTEXT:localhost:8085}")
    private String defaultContext;

    @Value("${VAPID_EMAIL_LINK_EXTERNAL_CONEXT:vapidpromotions.com}")
    private String externalContext;

    public  String getMpcMailFromAddr() {
        return MPC_MAIL_FROM_ADDR;
    }

    public String getDefaultContext() {
        return defaultContext;
    }


    public String getExternalContext() {
        return externalContext;
    }


    public  String getMpcMailFromName() {
        return MPC_MAIL_FROM_NAME;
    }

    public boolean isUseHttps() {
        return useHttps;
    }
}
