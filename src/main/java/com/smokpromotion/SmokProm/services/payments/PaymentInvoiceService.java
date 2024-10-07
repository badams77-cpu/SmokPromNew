package com.smokpromotion.SmokProm.services.payments;

import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrincipleService;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.DE_Invoice;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.domain.repo.REP_Invoice;
import com.smokpromotion.SmokProm.domain.repo.REP_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.email.SmtpMailSender;
import com.smokpromotion.SmokProm.services.email.AccessEmailer;
import com.smokpromotion.SmokProm.util.FileToZip;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentInvoiceService {

    private static final int MONTH_INVOICE_DATE = 28;

    private static final String TEMPLATE = "invoice";

    public static final double chargePerSend = 0.01;

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(PaymentInvoiceService.class);

    @Autowired
    private REP_UserService userService;

    @Autowired
    private REP_Invoice repInvoice;

    @Autowired
    private REP_SeduledTwitterSearch stSearchRepo;

    @Autowired
    private DR_EmailTemplate drEmailTemplate;

    @Autowired
    private VapidInvoiceGenerationService vapidInvoiceService;

    @Autowired
    private SmtpMailSender smtpMailSender;

    @Autowired
    private PortalSecurityPrincipleService portalSecurityPrincipleService;

    @Scheduled(cron="0 0 0 * * *")
    private void countMessages(){
        List<S_User> users = userService.getAll();
        LocalDate now = LocalDate.now();
        if (now.getDayOfMonth()!=28){
            return;
        }
        for(S_User user : users) {
            int userId = user.getId();
            List<DE_SeduledTwitterSearch> searches = stSearchRepo.getUsersSearchesInLastMonth(userId);
            if (searches.isEmpty()) {
                continue;
            }
            int nsent = 0;
            for(DE_SeduledTwitterSearch search : searches){
                nsent+= search.getNsent();
            }
            DE_Invoice inv = new DE_Invoice();
            inv.setInvoiceDate(LocalDate.now());
            inv.setUserId(userId);
            inv.setnSent(nsent);
            inv.setAmtCharged(chargePerSend*nsent);
            repInvoice.create(inv);

            Optional<DE_EmailTemplate> tempOpt = drEmailTemplate.getByNameAndLanguage(TEMPLATE, EmailLanguage.ENGLISH.name());

            PortalSecurityPrinciple principle = portalSecurityPrincipleService.create(user, "");

            Map<String, String> replacements = new HashMap<>();

            replacements.put("username", user.getFirstname()+" "+user.getLastname());

            replacements.put("invoice_date", inv.getInvoiceDate().format(DateTimeFormatter.ISO_DATE));

            try {

                FileToZip zip = vapidInvoiceService.generateForUser(principle, user, inv, searches);


                smtpMailSender.sendAttachmentTemplate(user.getUsername(), zip.getFile(),
                        "invoice.pdf", TEMPLATE,  EmailLanguage.ENGLISH, replacements);
            } catch (Exception e) {
                try {
                    smtpMailSender.sendTemplate(user.getUsername(), TEMPLATE, EmailLanguage.ENGLISH, replacements);
                } catch (Exception f) {
                }
            }
        }
        
    }


}
