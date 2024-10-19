package com.smokpromotion.SmokProm.analytics;

import com.smokpromotion.SmokProm.analytics.entity.RequestAnalyticsData;
import com.smokpromotion.SmokProm.analytics.repository.DR_RequestAnalyticsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Profile({"smok_app","smok_admin"})
@Service
public class AnalyticsSaverThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsSaverThread.class);

    private static final long SLEEP_MS = 10;

    private static final int ALL_POST_SIZE = 167781215;

    private Thread runner;
    private ConcurrentLinkedQueue<RequestAnalyticsData> queue;


    private final DR_RequestAnalyticsData drReceivedAnalytics;

    public AnalyticsSaverThread(DR_RequestAnalyticsData drReceivedAnalytics){
        this.drReceivedAnalytics = drReceivedAnalytics;
        this.queue = new ConcurrentLinkedQueue<>();
        this.runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }

    public void queue(RequestAnalyticsData data){
        queue.add(data);
    }

    public void run(){
        RequestAnalyticsData data = null;
        while(true){
            data = queue.poll();
            if (data==null){
                try {
                    Thread.sleep(SLEEP_MS);
                } catch (InterruptedException e){

                }
                continue;
            }
            try {
                if (data.getPostedVars()!=null && data.getPostedVars().length()>ALL_POST_SIZE){
                    data.setPostedVars(data.getPostedVars().substring(0, ALL_POST_SIZE));
                }
                drReceivedAnalytics.create( data);
            } catch (Exception e){
                LOGGER.error("Error saving analytics",e);
            }
        }
    }
}
