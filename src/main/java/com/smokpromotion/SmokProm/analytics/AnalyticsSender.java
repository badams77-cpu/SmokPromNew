package com.smokpromotion.SmokProm.analytics;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class, contains a queue of RequestAnalytic data to sent to mpcadmin
 * The data is sent by a thread, so as not to stall request, while waiting for a response
 */


public class AnalyticsSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsSender.class);

    private final String analyticsUrl;

    private final AnalyticsTokenService analyticsTokenService;

    private final ConcurrentLinkedQueue<RequestAnalyticsData> dataQueue;

    public final RestTemplate restTemplate;

    private final Thread runner;

    private boolean running;


    public AnalyticsSender(RestTemplate restTemplate, AnalyticsTokenService tokenService, String analyicsUrl ){
        this.restTemplate = restTemplate;
        this.analyticsUrl = analyicsUrl;
        this.analyticsTokenService = tokenService;
        dataQueue = new ConcurrentLinkedQueue<>();
        running = true;
        runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }

    public void queueData(RequestAnalyticsData data){
        dataQueue.add(data);
    }

    @Override
    public void run(){
        LOGGER.info("Starting Analytics Sender Thread");
        while(running){
            RequestAnalyticsData data = dataQueue.poll();
            if (data!=null) {
                if (!data.getAction().equalsIgnoreCase("/analytics")){
                    sendData(data);
                }
            } else {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {

                }
            }
        }
        LOGGER.info("Ended Analytics Sender Thread");
    }

    private void sendData(RequestAnalyticsData data){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LocalDate analyticDatumTime = LocalDate.now();

        try {
            data.setToken(analyticsTokenService.getTokenForDate(analyticDatumTime));
            LinkedHashMap<String, String> map= data.getDataMap();

            HttpEntity<LinkedHashMap<String, String>> request = new HttpEntity<LinkedHashMap<String, String>>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(analyticsUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.trace("Posted Request Analytics Data Successfully");
            } else {
                LOGGER.error("Failed Posting Analytics Data " + response.getStatusCode());
                LOGGER.error(response.getBody());
            }
        } catch (HttpServerErrorException e){
            LOGGER.error("sendData Analytics server error: "+e.getStatusCode());
            LOGGER.error(e.getResponseBodyAsString());
        } catch (HttpClientErrorException e){
            LOGGER.error("sendData: Analytics server error: "+e.getStatusCode());
            LOGGER.error(e.getResponseBodyAsString());
        } catch (Exception e){
            LOGGER.error("sendData: exception in sendData",e);
        }
    }
}
