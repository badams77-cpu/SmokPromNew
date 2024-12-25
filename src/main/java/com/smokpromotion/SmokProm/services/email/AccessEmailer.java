package com.smokpromotion.SmokProm.services.email;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
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
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
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

@Profile("smok_app")
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

    private TwitterCredentialsOAuth2 credentials;

    @Autowired
    public AccessEmailer(
            @Value("${twitoauth.clientId:}") String clientId,
            @Value("${twitoauth.clientSecret:}") String clientSecret,
            @Value("${twitter.accessToken:}") String accessToken,
            @Value("${twitter.accessSecret:}") String accessSecret
    ){
        credentials =  new  TwitterCredentialsOAuth2(clientId, clientSecret,
                accessToken,
                accessToken);
        credentials.setOAUth2AutoRefreshToken(true);
        if (clientId.equals("") || clientId.equals("")) {
            LOGGER.error("No twitoauth credentials access emailer");
        } else {
            LOGGER.error("Have twitoauth credentials access emailer");
        }
    }

    @Scheduled(cron="0 5 0,6,8,10,12,14,16,18,20 * * *")
    public void AccessEmailScheduledRun(){
        var conf = new ConfigurationBuilder()
                .setJSONStoreEnabled(true)
                .build();
        OAuthAuthorization oAuth = new OAuthAuthorization(conf);

        List<Integer> usersNeedingKeys = accessCodeRepo.getUserIdsLastDaysWithoutCodes();

        LOGGER.warn(usersNeedingKeys+" users needing keys");

        //     Optional<DE_EmailTemplate> template = emailTemplateRepo.getByNameAndLanguage(TEMPLATE, EmailLanguage.ENGLISH.getLabel());
   //     if (template.isEmpty()){
   //         LOGGER.warn("Email Template "+TEMPLATE+" Not found");
   //         return;
   //     }
        String url="https://www.vapidpromotions.com/";
        for(int uid : usersNeedingKeys) {
            try {

                PKCE pkce = new PKCE();
                pkce.setCodeChallenge("challenge");
                pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
                pkce.setCodeVerifier("challenge");

                S_User user = userService.getById(uid);

                if (user.getSubCount()==0){ continue; }

                String email = user.getUsername();

                String authToken = getAuthUrl( credentials, pkce, 0);

                DE_AccessCode codeEntity = new DE_AccessCode();
                codeEntity.setUserId(uid);
                codeEntity.setCodeDate(LocalDate.now());
  //              codeEntity.setRequestToken(pkce.getCodeChallenge());
                codeEntity.setRequestToken(authToken);
                accessCodeRepo.create(codeEntity);

                Map<String, String> replaceMap = new HashMap<>();

                replaceMap.put("username", user.getFirstname()+" "+user.getLastname());


                String emailBody = generateMessageBody(authToken, user, url);

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
private String generateMessageBody(String tokenUrl, S_User user, String url ) {
    String body = "";
    if (!GenericUtils.isNull(body)) {
        String conString =
                url.toString().replace(emailConfig.getDefaultContext(),
                        emailConfig.getExternalContext());
        String hashed="";
        String url1 = emailConfig.isUseHttps() ? conString.replace("http:", "https:") : conString;
        body+="<html><head><title></title></head><body> " +
                "<p>Dear "+user.getFirstname()+" "+user.getLastname()+", </p>"+
                "<p>"+
                "We have sent you this message because you requested that you have search results that need messages sent</p>"+
                "<p>"+
                "To do this, click the link below to authorise twitter to send your messages. </p>"+
                "<a href='"+tokenUrl+"' mc:disable-tracking  > Click here to authorise twitter to send your messages </a></p>"+
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
    public String getAuthUrl(TwitterCredentialsOAuth2 credentials, PKCE pkce, int accessId) {
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://www.vapidpromotions.com/tcallback",
                "offline.access dm.write tweet.read users.read tweet.write");
        OAuth2AccessToken accessToken = null;
        String authorizationUrl;
        try {

            System.out.println("Fetching the Authorization URL...");

            final String secretState = "state_code_"+accessId;

            authorizationUrl = service.getAuthorizationUrl(pkce, secretState);

        } catch (Exception e) {
            LOGGER.warn("Exceotion geting authorization URL", e);
            throw new RuntimeException(e);

        }
        return authorizationUrl;
    }
}
