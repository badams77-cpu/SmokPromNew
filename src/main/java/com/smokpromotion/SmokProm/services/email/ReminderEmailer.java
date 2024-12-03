package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.domain.repo.REP_AccessCode;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.email.SmtpMailSender;
import com.smokpromotion.SmokProm.email.SmtpMailWrapper;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;

@Profile("smok_App")
@Service
public class ReminderEmailer {

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(ReminderEmailer.class);

    private static final String TEMPLATE = "subscription_reminder_email";
    @Autowired
    private SmtpMailWrapper smtpMailSender;

    @Autowired
    private PortalEmailConfig emailConfig;

    @Autowired
    private REP_AccessCode accessCodeRepo;

    @Autowired
    private DR_EmailTemplate emailTemplateRepo;

    @Autowired
    private REP_TwitterSearch twitterSearchRepo;

    @Autowired
    private REP_UserService userService;

    @Value("${twitter_consumerKey:}")
    private String consumerKey;

    @Value("${twitter_consumerSecret:}")
    private String consumerSecret;

    @Autowired
    private SmtpMailSender mailSender;

    public ReminderEmailer(){

    }

    @Scheduled(cron="0 30 7 * * *")
    public void SubscriptionReminder(){
        List<S_User> usersIn = userService.getAll();
        List<Integer> userDelinq = new LinkedList<>();
        Map<Integer, String> userMessages = new HashMap<>();
        for(S_User user : usersIn) {
            List<DE_TwitterSearch> tw =twitterSearchRepo.findByUserIdActive(user.getId());
            if (user.getSubCount()<tw.size()) {
               userMessages.put(user.getId(), "Sorry you have "+tw.size()+" searches active but only "+user.getSubCount()+" subscriptions");
            }
            userDelinq.add(user.getId());
        }
for (int uid : userDelinq) {
            try {

                S_User user = userService.getById(uid);

                String email = user.getUsername();

                DE_AccessCode codeEntity = new DE_AccessCode();

                Map<String, String> replaceMap = new HashMap<>();

                replaceMap.put("username", user.getFirstname()+" "+user.getLastname());
                replaceMap.put("message", userMessages.get(uid));
                replaceMap.put("billing_page", "/a/billing");

                mailSender.sendTemplate(email, TEMPLATE, EmailLanguage.ENGLISH,  replaceMap);

            } catch (UserNotFoundException e) {
                LOGGER.warn("UserId "+uid+ "not found",e);
        //    } catch (TwitterException e) {
        //        LOGGER.warn(" twitter exception getting request Token",e);
            } catch (MessagingException e){
                LOGGER.warn(" exception sending message",e);
            }
        }

    }

//     Remember for more searches than subs email.


}
