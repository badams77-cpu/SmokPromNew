package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.AccessToken;
import twitter4j.OAuthAuthorization;
import twitter4j.RequestToken;

@Service
public class AccessEmailer {

    @Autowired
    private SmtpMailWrapper smtpMailSender;

    @Autowired
    private PortalEmailConfig emailConfig;


    @Value("${twitter_consumerKey:}")
    private String consumerKey;

    @Value("${twitter_consumerSecret:}")
    private String consumerSecret;


    public AccessEmailer(){

    }

    @Scheduled(cron="0 5,15,25,35,45,55 * * * *")
    public void AccessEmailScheduledRun(){
        OAuthAuthorization oAuth = OAuthAuthorization.newBuilder()
                .oAuthConsumer(consumerKey, consumerSecret).build();
        RequestToken requestToken = oAuth.getOAuthRequestToken();
        AccessToken accessToken = null;




    }


}
