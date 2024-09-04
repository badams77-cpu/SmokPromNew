package com.smokpromotion.SmokProm.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Profile({"admin","dxpulse_admin"})
public class AnalyticsReceiverController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsReceiverController.class);

    private final AnalyticsSaverThread saver;

    private final AnalyticsTokenService tokenService;

    @Autowired
    public AnalyticsReceiverController( AnalyticsSaverThread saver, AnalyticsTokenService tokenService){
        this.saver = saver;
        this.tokenService = tokenService;
    }

    @PostMapping(path="/analytics")
    public ResponseEntity<Void> processReceipt(@RequestBody Map<String, String> paramMap ) {
        try {
            RequestAnalyticsData data = new RequestAnalyticsData(paramMap);
            if (!tokenService.checkToken(data.getToken(), data.getDate())){
                LOGGER.error("processReceipt: Analytics token incorrect");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            saver.queue(data);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MissingParameterException | ExtraParameterException e){
            LOGGER.error("processReceipt: Missing or Extra Parameters ",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            LOGGER.error("processReceipt: Exception processing analytics request ",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
