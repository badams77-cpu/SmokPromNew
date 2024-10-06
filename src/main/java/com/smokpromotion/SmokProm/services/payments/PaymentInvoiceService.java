package com.smokpromotion.SmokProm.services.payments;

import com.smokpromotion.SmokProm.domain.entity.DE_Invoice;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_Invoice;
import com.smokpromotion.SmokProm.domain.repo.REP_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.services.email.AccessEmailer;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentInvoiceService {

    private static final int MONTH_INVOICE_DATE = 28;

    private static final double chargePerSend = 0.01;

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(PaymentInvoiceService.class);

    @Autowired
    private REP_UserService userService;

    @Autowired
    private REP_Invoice repInvoice;

    @Autowired
    private REP_SeduledTwitterSearch stSearchRepo;

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

        }
        
    }


}
