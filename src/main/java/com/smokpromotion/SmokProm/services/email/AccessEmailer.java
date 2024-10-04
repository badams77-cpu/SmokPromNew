package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.domain.repo.REP_AccessCode;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.AccessToken;
import twitter4j.OAuthAuthorization;
import twitter4j.RequestToken;
import twitter4j.TwitterException;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccessEmailer {

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
        OAuthAuthorization oAuth = OAuthAuthorization.newBuilder()
                .oAuthConsumer(consumerKey, consumerSecret).build();

        List<Integer> usersNeedingKeys = accessCodeRepo.getUserIdsLast7DaysWithoutCodes();
        Optional<DE_EmailTemplate> template = emailTemplateRepo.getByNameAndLanguage(TEMPLATE, EmailLanguage.ENGLISH.getLabel());
        if (template.isEmpty()){
            LOGGER.warn("Email Template "+TEMPLATE+" Not found");
            return;
        }
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

                mailSender.sendTemplate(email, TEMPLATE, EmailLanguage.ENGLISH,  replaceMap);

            } catch (UserNotFoundException e) {
                LOGGER.warn("UserId "+uid+ "not found",e);
            } catch (TwitterException e) {
                LOGGER.warn(" twitter exception getting request Token",e);
            } catch (MessagingException e){
                LOGGER.warn(" exception sending message",e);
            }
        }

    }


}
