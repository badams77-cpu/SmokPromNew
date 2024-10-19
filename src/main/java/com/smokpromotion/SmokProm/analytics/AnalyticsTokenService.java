package com.smokpromotion.SmokProm.analytics;

import com.smokpromotion.SmokProm.analytics.repository.DR_AnalyticsToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class AnalyticsTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsTokenService.class);

    private Map<LocalDate, UUID> tokensByDate;

    private DR_AnalyticsToken drAnalyticsToken;

    private boolean receiver;

    @Autowired
    public AnalyticsTokenService(DR_AnalyticsToken drAnalyticsToken, @Value("${MPC_ANALYTICS_RECEIVER:false}") boolean receiver){
        this.drAnalyticsToken = drAnalyticsToken;
        tokensByDate = new HashMap<LocalDate, UUID>();
        this.receiver = receiver;
        setToken();
    }

    // Runs 2 minute before midnight to set token for the next day
    @Scheduled(cron="0 58 23 * * ?")
    public void setToken(){
        if (receiver) {
            LocalDateTime inThreeMinutes = LocalDateTime.now().plusMinutes(3);
            UUID token = UUID.randomUUID();
            try {
                if (drAnalyticsToken.getForDate( inThreeMinutes.toLocalDate())==null) {
                    drAnalyticsToken.save( inThreeMinutes.toLocalDate(), token);
                }
                LocalDate today = LocalDate.now();
                drAnalyticsToken.deleteOld( LocalDate.now());
                tokensByDate = tokensByDate.entrySet().stream().filter(x->!x.getKey().isBefore(today) ).collect(Collectors.toMap(x->x.getKey(),x->x.getValue()));
                LOGGER.info("setToken: Set new Analytics Token, cleared old");
            } catch (Exception e){
                LOGGER.error("setToken: Error saving new token or deleting old",e);
            }
        }
    }

    public String getTokenForDate(LocalDate localDate){
        UUID token = tokensByDate.get(localDate);
        if (token!=null){
            return token.toString();
        }
        token = drAnalyticsToken.getForDate( localDate);
        if (token!=null){
            tokensByDate.put(localDate, token);
            return token.toString();
        }
        setToken();
        token = drAnalyticsToken.getForDate( localDate);
        return token==null ? "" : token.toString();
    }

    public boolean checkToken(String checkToken, LocalDate date){
        String token = getTokenForDate(date);
        if (token==null){ return false; }
        return token.equals(checkToken);
    }

}
