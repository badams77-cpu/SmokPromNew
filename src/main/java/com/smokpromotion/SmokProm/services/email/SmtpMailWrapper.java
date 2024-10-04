package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Map;
import java.util.Random;

@Service
@Profile("!test")
public class SmtpMailWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMailWrapper.class);

    private final SmtpMailSender sender;

    private final boolean stubEmails;

    private final long delay;

    private final double percentFailure;

    private final Random random;

    @Autowired
    public SmtpMailWrapper(SmtpMailSender sender,
                           @Value("${MPC_STUB_EMAILS:false}") boolean stubEmails,
                           @Value("${MPC_STUB_EMAIL_DELAY_MILLIS:500}") long delay,
                           @Value("${MPC_STUB_EMAIL_PERCENT_FAILURE:0.0}") double percentFailure
    ){
        this.sender = sender;
        this.stubEmails = stubEmails;
        this.delay = delay;
        this.percentFailure = percentFailure;
        random = new Random(System.currentTimeMillis());
    }

    public boolean sendTemplate(String to, String template, EmailLanguage language, Map<String,String> replaceMap) throws MessagingException {
        if (!stubEmails){
            return sender.sendTemplate(to, template, language, replaceMap);
        }
        if (random.nextDouble()*100.0<percentFailure){
            LOGGER.debug("Stub email only - message would fail to be sent to " + to);
            throw new MessagingException("Stub email - demonstrating failure for email send to  "+to);
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e){


        }
        LOGGER.debug("Stub email only - message would be sent successfully to " + to);
        return true;
    }

    public EmailPreview getEmailPreview(String to, String template, EmailLanguage language, Map<String,String> replaceMap) throws MessagingException {
        return sender.getEmailPreview(to, template, language, replaceMap);
    }
}
