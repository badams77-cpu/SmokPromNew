package com.smokpromotion.SmokProm.services.email;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.*;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.email.SmtpMailWrapper;
import com.smokpromotion.SmokProm.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Profile("portal")
@Service
public class MPCPayEmailGenerationService {

    private static final int SECONDS_IN_HALF_HOUR = 1800;

    private static final Logger LOGGER = LoggerFactory.getLogger(MPCPayEmailGenerationService.class);

    public static final String INVOICE_FINAL_TEMPLATE = "invoice_template";

    private static final String LOGIN_PAGE = "https://portalx.imultipractice.com/login";
    private static final String AU_LOGIN_PAGE = "https://portalx-au.imultipractice.com/login";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("EEEE dd MMMM yy "));
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm:ss EEEE dd MMMM yy"));
    public static final int MINUTES_IN_HOUR = 60;


    private final SmtpMailWrapper smtpMailSender;

    private final PortalEmailConfig emailConfig;

    private final DR_EmailTemplate drEmailTemplate;

    private ZoneId zoneId;
    private ZoneId systemZoneId;

    private final String from;

    @Autowired
    public MPCPayEmailGenerationService(
            DR_EmailTemplate drEmailTemplate,
            SmtpMailWrapper smptMailSender,
            @Value("${MPC_SCHEDULER_TIMEZONE:Europe/London}") String timezone,
            @Value("${_EMAILS_FROM_NAME:MPC}") String from,
            PortalEmailConfig emailConfig
    ) {
        this.drEmailTemplate = drEmailTemplate;
        this.emailConfig = emailConfig;
        this.smtpMailSender = smptMailSender;
        this.from = from;
        systemZoneId = ZoneId.systemDefault();
        if (timezone != null) {
            try {
                zoneId = ZoneId.of(timezone);
            } catch (Exception e) {
                LOGGER.error("MPCPayEmailGenerationService: Exception setting timezone: "+timezone, e);
                LOGGER.warn("MPCPayEmailGenerationService: Using System Default Timezone");
            }
        }
    }

    public EmailPreview generateEmailByProvider(S_User suser, DE_Invoice invoice, List<DE_SeduledTwitterSearch> searches){

        String email = suser.getUsername();
        if (email==null || email.isEmpty()) {
            return errorPreview("No Email Address");
        }



        return replaceAndSendEmail(suser.getUsername(), suser, invoice, searches, false, true);

    }

    private EmailPreview errorPreview(String s){
        EmailPreview ret = new EmailPreview();
        ret.setError(s);
        return ret;
    }


    private EmailPreview replaceAndSendEmail(
                                             String email,
                                             S_User sUser,
                                             DE_Invoice invoice,
                                             List<DE_SeduledTwitterSearch> searches,
                                             boolean preview,
                                             boolean addInvoice) {
        Map<String,String> replacements = new HashMap<>();
        replacements.put("date", LocalDate.now().format(DATE_FORMAT));
            replacements.put("invBusinessName", sUser.getFirstname()+" "+sUser.getLastname());
            replacements.put("invAddress1",sUser.getAddress1());
            replacements.put("ifInvAddress2", GenericUtils.getValid(sUser.getAddress2()));
            replacements.put("invAddress2",sUser.getAddress2());
            replacements.put("invTownCity",sUser.getPostcode());
            replacements.put("invStateCounty",sUser.getCountry());

        LOGGER.warn("replaceAndSendEmail: "+smtpMailSender);
        String loginpage = LOGIN_PAGE;
        if (emailConfig.isUseHttps()){
            loginpage = loginpage.replaceAll("http://", "https://");
        }
        replacements.put("loginpage", loginpage);
        replacements.put("startDate", invoice.getInvoiceDate().minusMonths(1).plusDays(1).format(DATE_FORMAT));
        replacements.put("endDate", invoice.getInvoiceDate().format(DATE_FORMAT));
        EmailPreview em = new EmailPreview();

        Optional<DE_EmailTemplate> template = drEmailTemplate.getByNameAndLanguage(INVOICE_FINAL_TEMPLATE, EmailLanguage.ENGLISH.name());

        em.setBody(template.orElse(new DE_EmailTemplate()).getTemplateBody());
        em.setSubject(template.orElse(new DE_EmailTemplate()).getSubject());
        em.setTo(email);
        em.setError(null);
        return em;
    }


/*
    private LocalDateTime adjustDBTimeToZone(LocalDateTime time){
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(time);
//        LocalDateTime dbTime = dbConnectionFactory.getDBTime(PortalEnum.AWS);
        LocalDateTime now = LocalDateTime.now();
        long dbOffset = Duration.between(dbTime, now).getSeconds();
        long roundOffset = SECONDS_IN_HALF_HOUR *(Math.round(dbOffset/SECONDS_IN_HALF_HOUR)); // Round to near half hour
        Calendar calendar = new GregorianCalendar();
        TimeZone systemTimeZone = calendar.getTimeZone();
        ZoneOffset zoneOffsetSystem = ZoneId.of(systemTimeZone.getID()).getRules().getOffset(time);
        return  time.plusSeconds(zoneOffset.getTotalSeconds()-zoneOffsetSystem.getTotalSeconds()+roundOffset);
    }
*/


}
