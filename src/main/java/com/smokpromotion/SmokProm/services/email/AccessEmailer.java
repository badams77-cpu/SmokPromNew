package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.controller.portal.PortalBaseController;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.domain.repo.REP_AccessCode;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.email.SmtpMailSender;
import com.smokpromotion.SmokProm.email.SmtpMailWrapper;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.GenericUtils;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.RequestMapping;
import twitter4j.HttpParameter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Profile("smok_App")
@Service
public class AccessEmailer {


        private static final String VAPID_LOGO="images/vapid-440x350.png";

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(AccessEmailer.class);

    private static final String TEMPLATE = "access_code_email";
    @Autowired
    private SmtpMailWrapper smtpMailSender;

    @Autowired
    private PortalEmailConfig emailConfig;

    @Autowired
    private REP_AccessCode accessCodeRepo;

    @Autowired
    private DR_EmailTemplate emailTemplateRepo;

    @Autowired
    private REP_UserService userService;

    @Value("${twitter_consumerKey:}")
    private String consumerKey;

    @Value("${twitter_consumerSecret:}")
    private String consumerSecret;

    @Autowired
    private SmtpMailSender mailSender;

    public AccessEmailer(){

    }

    @Scheduled(cron="0 5,15,25,35,45,55 * * * *")
    public void AccessEmailScheduledRun(){
        var conf = new ConfigurationBuilder()
                .setJSONStoreEnabled(true)
                .build();
        OAuthAuthorization oAuth = new OAuthAuthorization(conf);

        List<Integer> usersNeedingKeys = accessCodeRepo.getUserIdsLast7DaysWithoutCodes();

        LOGGER.warn(usersNeedingKeys+" users needing keys");

        //     Optional<DE_EmailTemplate> template = emailTemplateRepo.getByNameAndLanguage(TEMPLATE, EmailLanguage.ENGLISH.getLabel());
   //     if (template.isEmpty()){
   //         LOGGER.warn("Email Template "+TEMPLATE+" Not found");
   //         return;
   //     }
        String url="https://www.vapidpromotions.com/twitter_access";
        for(int uid : usersNeedingKeys) {
            try {
                RequestToken requestToken = oAuth.getOAuthRequestToken();
                AccessToken accessToken = null;

                S_User user = userService.getById(uid);

                String email = user.getUsername();

                DE_AccessCode codeEntity = new DE_AccessCode();
                codeEntity.setUserId(uid);
                codeEntity.setCodeDate(LocalDate.now());
                codeEntity.setRequestToken(requestToken.getToken());
                accessCodeRepo.create(codeEntity);

                Map<String, String> replaceMap = new HashMap<>();

                replaceMap.put("username", user.getFirstname()+" "+user.getLastname());
                replaceMap.put("access_url", requestToken.getAuthorizationURL());

                String emailBody = generateMessageBody(requestToken.getToken(), user, url);

                LOGGER.warn("Sending twitter access message to user "+user.getUsername());

                mailSender.send(emailConfig.getMpcMailFromAddr(), emailConfig.getMpcMailFromName(),
                        user.getUsername(),"Vapid Promotion - Twitter Access"  ,  emailBody);


                LOGGER.warn("Sent twitter access message to user "+user.getUsername());


            } catch (UserNotFoundException e) {
                LOGGER.warn("UserId "+uid+ "not found",e);
            } catch (TwitterException e) {
                LOGGER.warn(" twitter exception getting request Token",e);
            } catch (Exception e){
                LOGGER.warn(" exception sending message",e);
            }
        }

    }

//     Remember for more searches than subs email.
private String generateMessageBody(String token, S_User user, String url ) {
    String body = "";
    if (!GenericUtils.isNull(body)) {
        String conString = url.toString().replace(emailConfig.getDefaultContext(), emailConfig.getExternalContext());
        String hashed="";
        try {
            hashed = Base64.getUrlEncoder().encodeToString( token.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = emailConfig.isUseHttps() ? conString.replace("http:", "https:") : conString;
        body+="<html><head><title></title></head><body> " +
                "<p>Dear "+user.getFirstname()+" "+user.getLastname()+", </p>"+
                "<p>"+
                "We have sent you this message because you requested that you have search results that need messages sent</p>"+
                "<p>"+
                "To do this, click the link below to authorise twitter to send your messages. </p>"+
                "<a href='"+url+"?pr="+hashed+"' mc:disable-tracking  > Click here to authorise twitter to send your messages </a></p>"+
                "<p>"+
                "Thank You.</p>"+
                "<h3> Vapid Promotions Admin Team</h3>" +
                "<img src='"+url.replace("/twitter_access", "") + VAPID_LOGO+"'><br>"+

                "<br><br>"
                + "</body>"
                + "</html>";

    }
    return body;

}


}
