package com.smokpromotion.SmokProm.invoice_jasper;

import com.urcompliant.config.portal.PortalEmailConfig;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.config.portal.PortalSecurityPrincipleService;
import com.urcompliant.controller.portal.mpcpay.domain.ProviderEmailDetailData;
import com.urcompliant.domain.EmailLanguage;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_User;
import com.urcompliant.domain.entity.LanguageSettingEnum;
import com.urcompliant.domain.entity.mpcpay.*;
import com.urcompliant.domain.repository.mpcpay.*;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.practicedata.mpcpay.*;
import com.urcompliant.service.SmtpMailWrapper;
import com.urcompliant.util.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Profile({"portal"})
@Service
public class MPCPayEmailThreadPool implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MPCPayEmailThreadPool.class);

    private static final String EMAIL_TITLE = "MPC Pay - Payments Report";
    public static final String TEMPLATE = "MPCPay_ProviderEmail";
    public static final String DRAFT_TEMPLATE = "MPCPay_DraftProviderEmail";

    private static final String LOGIN_PAGE = "https://portalx.imultipractice.com/login";
    private static final String AU_LOGIN_PAGE = "https://portalx-au.imultipractice.com/login";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("EEEE dd MMMM yy "));
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm:ss EEEE dd MMMM yy"));
    public static final int MINUTES_IN_HOUR = 60;

    private final int poolSize;
    private final Thread[] runners;
    private final Queue<DE_PeriodProvider> runQueue;
    private final Map<DE_PeriodProvider, String> currencySymbolMap;
    private final Map<DE_PeriodProvider, PortalEnum> periodProviderPortals;
    private final Map<DE_PeriodProvider, LanguageSettingEnum> periodProviderLanguage;
    private final DR_PayPeriod drPayPeriod;
    private final DR_PeriodProvider drPeriodProvider;
    private final DR_MPCPayProvider drMpcPayProvider;



    private final boolean runAlerts;


    private ZoneId zoneId;
    private ZoneId systemZoneId;


    private final MPCPayEmailGenerationService emailService;



    @Autowired
    public MPCPayEmailThreadPool(

            DR_PayPeriod drPayPeriod,
            DR_MPCPayProvider drMpcPayProvider,
            DR_PeriodProvider drPeriodProvider,
            MPCPayEmailGenerationService emailService,
            @Value("${MPC_SCHEDULER_TIMEZONE:Europe/London}") String timezone,

            @Value("${MPC_PAY_EMAILS:false}") String runAlerts,
            @Value("${MPC_PAY_EMAILS_POOL_SIZE:5}") int poolSize

    ){
        this.poolSize = poolSize;
        this.runAlerts = Boolean.valueOf(runAlerts);
        currencySymbolMap = new HashMap();
        periodProviderPortals = new HashMap();
        periodProviderLanguage = new HashMap<>();
        this.emailService = emailService;
        this.drMpcPayProvider = drMpcPayProvider;
        this.drPayPeriod = drPayPeriod;
        this.drPeriodProvider = drPeriodProvider;
        runQueue = new ConcurrentLinkedQueue<DE_PeriodProvider>();
        zoneId = ZoneId.systemDefault();
        systemZoneId = ZoneId.systemDefault();
        if (timezone != null) {
            try {
                zoneId = ZoneId.of(timezone);
            } catch (Exception e) {
                LOGGER.error("MPCPayEmailTheadPool: Exception setting timezone: "+timezone, e);
                LOGGER.warn("MPCPayEmailThreadPool: Using System Default Timezone");
            }
        }
        if (Boolean.valueOf(runAlerts)){
            LOGGER.info("MPCPayEmailThreadPool: starting "+poolSize+" MPC Pay Email Pool Threads");
            runners = new Thread[poolSize];
            for(int i=0;i<poolSize;i++) {
                runners[i] = new Thread(this);
                runners[i].setDaemon(true);
                runners[i].start();
            }
        } else {
            runners = new Thread[0];
        }
    }

    /**
     * Queues to send notification emails to all providers in a pay period
     * @param principle the user running MPC Pay
     * @param payPeriod the pay period this is for
     * @return the number of providers added to the queue
     */
    public int queueEmailForPayPeriod(PortalSecurityPrinciple principle, DE_PayPeriod payPeriod, boolean addInvoice){
        // obtain the providers in the pay period.
        // Currently this is done in order of mpcPayProviderId. This is so that Int tests can expect the order in which these will be sent
        List<DE_PeriodProvider> periodProviders = drPeriodProvider.getAllForPeriod(principle.getPortal(), principle.getPracticeGroupId(), payPeriod.getId())
                                                    .stream().sorted(Comparator.comparing(DE_PeriodProvider::getMpcPayProviderId)).collect(Collectors.toList());
        for(DE_PeriodProvider provider : periodProviders){
            currencySymbolMap.put(provider, principle.getCurrencySymbol());
            periodProviderPortals.put(provider, principle.getPortal());
            periodProviderLanguage.put(provider, principle.getUserLanguage());
            provider.setAddInvoice(addInvoice);
            LOGGER.info("queueEmailForPayPeriod: practiceGroupId: " + provider.getPracticeGroupId() + " queued period Provider: "+provider.getId());
            runQueue.add(provider);
        }
        return periodProviders.size();
    }

    public boolean queueEmailForSingleProvider(PortalSecurityPrinciple principle, int periodId, int mpcPayProviderId, boolean addInvoice){
        Optional<DE_PeriodProvider> providerOpt = drPeriodProvider.getByPeriodIdAndProviderId(principle.getPortal(), principle.getPracticeGroupId(), periodId, mpcPayProviderId );
        if (!providerOpt.isPresent()){
            LOGGER.warn("queueEmailForSingleProvider: practiceGroupId: " + principle.getPracticeGroupId() + " period mpcPayProviderId: "+mpcPayProviderId+" not found in periodId: " + periodId);
            return false;
        }
        DE_PeriodProvider provider = providerOpt.get();
        currencySymbolMap.put(provider, principle.getCurrencySymbol());
        periodProviderPortals.put(provider, principle.getPortal());
        periodProviderLanguage.put(provider, principle.getUserLanguage());
        provider.setAddInvoice(addInvoice);
        LOGGER.info("queueEmailForSingleProvider: practiceGroupId: " + provider.getPracticeGroupId() + " queued period Provider: "+provider.getId());
        runQueue.add(provider);
        return true;
    }


    public void run(){
        DE_PeriodProvider periodProvider = null;
        while( true){
            LocalDateTime runDateTime = adjustTimeToZone(LocalDateTime.now());
            periodProvider = runQueue.poll();
            if (periodProvider==null){
                sleep(100L);
                continue;
            }
            PortalEnum portal = periodProviderPortals.get(periodProvider);
            Optional<DE_MPCPayProvider> provider = drMpcPayProvider.getByProviderId(portal, periodProvider.getPracticeGroupId(), periodProvider.getMpcPayProviderId());
            Optional<DE_PayPeriod> payPeriod = drPayPeriod.getById(portal, periodProvider.getPracticeGroupId(), periodProvider.getPeriodId());
            if (!provider.isPresent()){
                LOGGER.error("run: practiceGroupId: " + periodProvider.getPracticeGroupId() + " payPeriod "+periodProvider.getPeriodId()+" mpcPayProviderId "+periodProvider.getMpcPayProviderId()+" not found");
                continue;
            }
            if (!payPeriod.isPresent()){
                LOGGER.error("run: practiceGroupId: " + periodProvider.getPracticeGroupId() + "payPeriod "+periodProvider.getPeriodId()+" not found");
                continue;
            }
            emailService.generateEmailByProvider(portal, periodProvider, provider.get(), payPeriod.get(), periodProviderLanguage.get(periodProvider), currencySymbolMap.get(periodProvider),false, periodProvider.isAddInvoice());
            currencySymbolMap.remove(periodProvider);
            periodProviderPortals.remove(periodProvider);
            periodProviderLanguage.remove(periodProvider);
        }

    }



    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e){
            // If this happens often stacktrace would full up the log file
            LOGGER.debug("sleep: Sleep interrupted ");
        }
    }


    private LocalDateTime adjustTimeToZone(LocalDateTime time){
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(time);
        ZoneOffset systemZoneOffset = systemZoneId.getRules().getOffset(time);
        return  time.plusSeconds(zoneOffset.getTotalSeconds()-systemZoneOffset.getTotalSeconds());
    }

}
