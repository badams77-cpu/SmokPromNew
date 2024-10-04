package com.smokpromotion.SmokProm.services.email;

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

    public static final String TEMPLATE = "MPCPay_ProviderEmail";
    public static final String INVOICE_FINAL_TEMPLATE = "MPCPay_ProviderEmail_WithInvoice";
    public static final String DRAFT_TEMPLATE = "MPCPay_DraftProviderEmail";

    private static final String LOGIN_PAGE = "https://portalx.imultipractice.com/login";
    private static final String AU_LOGIN_PAGE = "https://portalx-au.imultipractice.com/login";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("EEEE dd MMMM yy "));
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm:ss EEEE dd MMMM yy"));
    public static final int MINUTES_IN_HOUR = 60;

    /*
    private final DR_PayPeriod drPayPeriod;
    private final DR_PeriodProvider drPeriodProvider;
    private final DR_PayCalculationDataRow drPayCalculationDataRow;
    private final DR_ResultView drResultView;
    private final DR_MPCPayProvider drMpcPayProvider;
    private final DR_MPCPayPractice drMpcPayPractice;
    private final DR_MPCPayEmailLog drMpcPayEmailLog;
    private final DR_MPCPayPeriodNote drMpcPayPeriodNote;
    private final ProviderPayEmailDetailService providerPayEmailDetailService;
    private final ProviderPayDetailService providerPayDetailService;
    private final PortalSecurityPrincipleService portalSecurityPrincipleService;
    private final DS_UserService dsUserService;
    private final DR_ProviderKpi drProviderKpi;
    private final MPCAppDBConnectionFactory dbConnectionFactory;

    private final SmtpMailWrapper smtpMailSender;

    private final PortalEmailConfig emailConfig;

    private final String overrideEmail;


    private ZoneId zoneId;
    private ZoneId systemZoneId;

    private final String from;

    @Autowired
    public MPCPayEmailGenerationService(

            SmtpMailWrapper smptMailSender,
            DR_PayPeriod drPayPeriod,
            DR_PayCalculationDataRow drPayCalculationDataRow,
            DR_ResultView drResultView,
            DR_MPCPayProvider drMpcPayProvider,
            DR_PeriodProvider drPeriodProvider,
            DR_MPCPayPractice drMpcPayPractice,
            DR_MPCPayEmailLog drMpcPayEmailLog,
            DR_MPCPayPeriodNote drMpcPayPeriodNote,
            DS_UserService dsUserService,
            ProviderPayEmailDetailService providerPayEmailDetailService,
            ProviderPayDetailService providerPayDetailService,
            PortalSecurityPrincipleService principleService,
            DR_ProviderKpi drProviderKpi,
            @Value("${MPC_SCHEDULER_TIMEZONE:Europe/London}") String timezone,
            @Value("${MPC_PAY_EMAILS_FROM_NAME:MPC}") String from,
            @Value("${MPC_PAY_TEST_EMAIL_ADDRESS:}") String overrideEmail,
            MPCAppDBConnectionFactory dbConnectionFactory,
            PortalEmailConfig emailConfig
    ) {
        this.drPayCalculationDataRow = drPayCalculationDataRow;
        this.drResultView = drResultView;
        this.drMpcPayProvider = drMpcPayProvider;
        this.drMpcPayPractice = drMpcPayPractice;
        this.drMpcPayEmailLog = drMpcPayEmailLog;
        this.drMpcPayPeriodNote = drMpcPayPeriodNote;
        this.drPeriodProvider = drPeriodProvider;
        this.drPayPeriod = drPayPeriod;
        this.providerPayEmailDetailService = providerPayEmailDetailService;
        this.providerPayDetailService = providerPayDetailService;
        this.dsUserService = dsUserService;
        this.portalSecurityPrincipleService = principleService;
        this.overrideEmail = overrideEmail;
        this.drProviderKpi = drProviderKpi;
        this.emailConfig = emailConfig;
        this.smtpMailSender = smptMailSender;
        this.from = from;
        this.dbConnectionFactory = dbConnectionFactory;
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

    public EmailPreview generateEmailByProvider(
            PortalEnum portal,
            DE_PeriodProvider periodProvider,
            DE_MPCPayProvider provider,
            DE_PayPeriod payPeriod, LanguageSettingEnum language, String currency, boolean preview, boolean addInvoice){
        DE_MPCPayEmailLog log = new DE_MPCPayEmailLog();
        log.setPracticeGroupId(provider.getPracticeGroupId());
        log.setMpcPayProviderId(provider.getId());
        log.setPeriodId(payPeriod.getId());
        log.setEmailStatus("");
        String email = (overrideEmail== null|| overrideEmail.isEmpty())? provider.getEmail() : overrideEmail;
        if (email==null || email.isEmpty()) {
            LOGGER.warn("generateEmailByProvider: practiceGroupId: " + provider.getPracticeGroupId() + ", provider does not have email address  not sending email, payPeriodId "+payPeriod.getId()+", mpcPayProviderId "+provider.getId());
            if (!preview) logError(portal, log, "Provider "+provider.getName()+" has no email address unable to send");
            return errorPreview("No Email Address");
        }

        PayCalculationData data = null;
        Optional<DE_ResultView> latestView = drResultView.getLatestForPeriod(portal, provider.getPracticeGroupId(), payPeriod.getId());
        if (latestView.isPresent()) {
            data = drPayCalculationDataRow.getDataByProvider(portal, provider.getPracticeGroupId(), payPeriod.getId(), latestView.get().getId(), provider.getId());
        } else {
            LOGGER.error("generateEmailByProvider: practiceGroupId: " + provider.getPracticeGroupId() + ", payPeriod "+periodProvider.getPeriodId()+" no  results found for mpcPayProviderId "+provider.getId());
            if (!preview) logError(portal, log,"No results have been generated yet for period: "+payPeriod.getDescription());
            return errorPreview("No Results For Period");
        }

        if (data.getRows().isEmpty()){
            LOGGER.warn("generateEmailByProvider: practiceGroupId: " + provider.getPracticeGroupId() + ", No Results in this pay period for Provider "+provider.getName()+" Period: "+payPeriod.getId()+" mpcPayProviderId: "+provider.getId());
            if (!preview) logError(portal, log, "No Results in this pay period for Provider "+provider.getName()+", nothing to Send");
            return errorPreview("No Result in period for Provider");
        }

        Optional<DE_ProviderKpi> providerKpi = drProviderKpi.getByProviderAndPeriod(portal, provider.getPracticeGroupId(), provider.getId(), payPeriod.getId());
        ProviderEmailDetailData provData = providerPayEmailDetailService.execute(currency==null ?"£":currency, data, payPeriod, provider.getId());
        ProviderDetailData payDetail= providerPayDetailService.execute(currency==null ?"£":currency, data, payPeriod,true);
        return replaceAndSendEmail(portal, log, email, periodProvider, provider, provData,  providerKpi, payDetail, payPeriod, latestView.get(), currency==null ?"£":currency, language, preview, addInvoice);

    }

    private EmailPreview errorPreview(String s){
        EmailPreview ret = new EmailPreview();
        ret.setError(s);
        return ret;
    }


    private void logError(PortalEnum portal, DE_MPCPayEmailLog log, String error){
        log.setEmailStatus(log.getEmailStatus()+error);
        log.setSuccess(false);
        log.setError(true);
        int ret=drMpcPayEmailLog.create(portal, log);
    }

    private void logSuccess(PortalEnum portal, DE_MPCPayEmailLog log, String error){
        log.setEmailStatus(log.getEmailStatus()+error);
        log.setError(false);
        log.setSuccess(true);
        int ret=drMpcPayEmailLog.create(portal, log);
    }

    private EmailPreview replaceAndSendEmail(PortalEnum portal,
                                             DE_MPCPayEmailLog log,
                                             String email,
                                             DE_PeriodProvider periodProvider,
                                             DE_MPCPayProvider provider,
                                             ProviderEmailDetailData providerDetailData,
                                             Optional<DE_ProviderKpi> providerKpi,
                                             ProviderDetailData payDetail,
                                             DE_PayPeriod payPeriod,
                                             DE_ResultView resultView,
                                             String currency,
                                             LanguageSettingEnum languageIn,
                                             boolean preview,
                                             boolean addInvoice) {
        Map<String,String> replacements = new HashMap<>();
        replacements.put("practiceName",  provider.getPracticeName());
        replacements.put("ifLocked",""+(payPeriod.getPeriodStatus()==PeriodStatus.LOCKED));
        replacements.put("lockedDate", (payPeriod.getLockedDateTime()==null? "Never" :adjustDBTimeToZone(payPeriod.getLockedDateTime()).format(DATE_FORMAT)));
        replacements.put("date", LocalDate.now().format(DATE_FORMAT));
        replacements.put("generatedDate", adjustDBTimeToZone(resultView.getViewDate()).format(DATETIME_FORMAT));
        replacements.put("periodTitle", payPeriod.getDescription());
        replacements.put("providerName", provider.getName());
        replacements.put("practiceName", provider.getPracticeName());
        String alias =provider.getAlias();
        if (alias==null || alias.equals("")){
            alias = provider.getName();
        }
        replacements.put("alias", alias);
        try {
            Optional<DE_MPCPayPeriodNote> periodNote = getProviderNote(portal, provider, payPeriod);
            if (periodNote.isPresent()){
                replacements.put("providerNote", periodNote.get().getContent());
                replacements.put("ifProviderNote", "true");
            } else {
                replacements.put("ifProviderNote", "false");
            }

        } catch (Exception e){
            LOGGER.warn("replaceAndSendEmail: (providerNotes) exception ",e);
            logError(portal, log, "Failed getting provider Notes");
        }
        Optional<DE_MPCPayPractice> optPractice = drMpcPayPractice.getByPracticeId(portal, provider.getPracticeGroupId(), provider.getPracticeId());
        if (optPractice.isPresent()){
            DE_MPCPayPractice prac = optPractice.get();
            replacements.put("practiceBusinessName", prac.getPracticeBusinessName());
            replacements.put("practiceAddress1",prac.getAddress1());
            replacements.put("ifPracticeAddress2", GenericUtils.getValid(prac.getAddress2()));
            replacements.put("practiceAddress2",prac.getAddress2());
            replacements.put("practiceTownCity",prac.getTownCity());
            replacements.put("practiceStateCounty",prac.getCountyState());
            replacements.put("practicePostCode",prac.getPostCode());
        }
        replacements.put("providerBusinessName", provider.getProviderBusinessName());
        replacements.put("providerAddress1",provider.getAddress1());
        replacements.put("ifProviderAddress2", GenericUtils.getValid(provider.getAddress2()));
        replacements.put("providerAddress2",provider.getAddress2());
        replacements.put("providerTownCity",provider.getTownCity());
        replacements.put("providerStateCounty",provider.getCountyState());
        replacements.put("providerPostCode",provider.getPostCode());
        LOGGER.warn("replaceAndSendEmail: "+smtpMailSender);
        String loginpage = LOGIN_PAGE;
        if (emailConfig.isUseHttps()){
            loginpage = loginpage.replaceAll("http://", "https://");
        }
        replacements.put("loginpage", loginpage);
        replacements.put("mpcsite", loginpage.replaceAll("^https?://","").replaceAll("/login$",""));
        replacements.put("startDate",payPeriod.getStartDate().format(DATE_FORMAT));
        replacements.put("endDate",payPeriod.getEndDate().format(DATE_FORMAT));
        replacements.putAll(payDetail.toDataMap(currency));
        replacements.putAll(providerDetailData.toDataMap(currency));
        replacements.put("ifDebt",""+providerKpi.isPresent());
        replacements.put("ifStats", ""+ providerKpi.isPresent());
        if (providerKpi.isPresent()){
            replacements.putAll(providerKpi.get().toDataMap(currency));
        }
        EmailLanguage language = (languageIn==null? LanguageSettingEnum.ENGLISH : languageIn).getEmailLanguage();
        String draft = payPeriod.getPeriodStatus()== PeriodStatus.LOCKED ? "Final" : "Provisional";
        String template = payPeriod.getPeriodStatus() == PeriodStatus.LOCKED ? TEMPLATE : DRAFT_TEMPLATE;
        if (payPeriod.getPeriodStatus()==PeriodStatus.LOCKED && addInvoice){
            template = INVOICE_FINAL_TEMPLATE;
        }
        try {
            if (!preview) {
                smtpMailSender.sendTemplate(email, template, language, replacements);
            } else {
                // URC-1857 refactor to return template for
                return smtpMailSender.getEmailPreview(email, template, language, replacements);
            }
            LOGGER.warn("replaceAndSendEmail: practiceGroupId: " + provider.getPracticeGroupId() + " provider "+provider.getName()+", mpcPayProviderId="+provider.getId() +", "+draft+" Mail "+"successfully sent to " + email);
            if (!preview) {
                LOGGER.warn("replaceAndSendEmail: sent email to "+email);
                logSuccess(portal, log, draft + " Mail successfully sent to " + email + " for provider " + provider.getName());
            }
        } catch (Exception e) {
            LOGGER.error("replaceAndSendEmail: practiceGroupId: " + provider.getPracticeGroupId() + " provider "+provider.getName()+", mpcPayProviderId="+provider.getId()+" - Error sending MPC Pay email to: " + email, e);
            if (!preview) logError(portal,log, draft+" Mail Failed to send to " + email+ " for provider "+provider.getName()+e.getMessage()+e.getStackTrace());
            return errorPreview("Exception Sending Message, does template exist?");
        }
        return null;
    }

    private Optional<DE_MPCPayPeriodNote> getProviderNote(PortalEnum portal, DE_MPCPayProvider provider, DE_PayPeriod period){
        List<DE_MPCPayPeriodNote> notes = drMpcPayPeriodNote.getForProviderAndPeriod(portal, provider.getPracticeGroupId(), provider.getPracticeId(), provider.getId(), period.getId(),false);
        if (notes.isEmpty()){ return Optional.empty(); }
        Optional<DE_User> userOpt = dsUserService.getById(portal, notes.get(0).getCreatedUserId());
        if (!userOpt.isPresent()){
            LOGGER.warn("Cannot get provider notes provider "+provider.getProviderCode()+" practice "+provider.getPracticeName()+" as its created by user id "+notes.get(0).getCreatedUserId()+" doesn't exist");
            return Optional.empty();
        }
        DE_User user = userOpt.get();
        if (portal==PortalEnum.AWS) {
            user.setPortalDdAws();
        } else {
            user.setPortalDbDc();
        }
        PortalSecurityPrinciple principle = portalSecurityPrincipleService.create(user, "");
        DE_MPCPayPeriodNote note = notes.get(0);
        try {
            return Optional.of(drMpcPayPeriodNote.decrypt(principle, note, false));
        } catch (CryptoException e){
            LOGGER.warn("Cannot get provider notes provider "+provider.getProviderCode()+" practice "+provider.getPracticeName()+" as cannot decrypt");
        }
        return Optional.empty();
    }

 //   private LocalDateTime adjustTimeToZone(LocalDateTime time){
 //       ZoneOffset zoneOffset = zoneId.getRules().getOffset(time);
 //       ZoneOffset systemZoneOffset = systemZoneId.getRules().getOffset(time);
 //       return  time.plusSeconds(zoneOffset.getTotalSeconds()-systemZoneOffset.getTotalSeconds());
 //   }

    private LocalDateTime adjustDBTimeToZone(LocalDateTime time){
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(time);
        LocalDateTime dbTime = dbConnectionFactory.getDBTime(PortalEnum.AWS);
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
