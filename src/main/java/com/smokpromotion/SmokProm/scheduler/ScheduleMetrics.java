package com.smokpromotion.SmokProm.scheduler;

import com.urcompliant.domain.PortalEnum;
import com.urcompliant.scheduler.dao.DE_SchedulerMetrics;
import com.urcompliant.scheduler.repository.DR_SchedulerMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Profile({"portal","admin"})
public class ScheduleMetrics {


    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleMetrics.class);

    private static final int MAX_QUEUE_SIZE = 100;

    private AtomicInteger runCount;
    private AtomicInteger failureCount;
    private AtomicInteger successCount;
    private AtomicInteger secondsRunning;
    private AtomicInteger secondsNotRunning;
    private AtomicInteger secondsQueued;
    private AtomicInteger secondsQueueEmpty;
    private AtomicInteger maxQueueSize;

    private final DR_SchedulerMetrics drSchedulerMetrics;

    private MPCReportScheduler mpcReportScheduler;


    @Autowired
    public ScheduleMetrics(DR_SchedulerMetrics drSchedulerMetrics){
        this.drSchedulerMetrics = drSchedulerMetrics;
        failureCount=new AtomicInteger(0);
        runCount=new AtomicInteger(0);
        successCount=new AtomicInteger(0);
        secondsQueued=new AtomicInteger(0);
        secondsQueueEmpty=new AtomicInteger(0);
        secondsRunning=new AtomicInteger(0);
        secondsNotRunning=new AtomicInteger(0);
        initializeMetrics(LocalDate.now());

    }

    public void setMpcReportScheduler(MPCReportScheduler mpcReportScheduler){
        this.mpcReportScheduler = mpcReportScheduler;
    }

    public void incRunCount(){
        runCount.incrementAndGet();
    }

    public void incSuccessCount(){
        successCount.incrementAndGet();
    }

    public void incFailureCount(){
        failureCount.incrementAndGet();
    }


    // Run every hour to save metric to database and reset at midnight
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledMetrics(){
        DE_SchedulerMetrics metrics = getMetrics();
        drSchedulerMetrics.saveEntry(PortalEnum.AWS, metrics);
        if (LocalDateTime.now().getHour()==0){
            // Reset count at midnight
            failureCount=new AtomicInteger(0);
            runCount=new AtomicInteger(0);
            successCount=new AtomicInteger(0);
            secondsQueued=new AtomicInteger(0);
            secondsQueueEmpty=new AtomicInteger(0);
            secondsRunning=new AtomicInteger(0);
            secondsNotRunning=new AtomicInteger(0);
        }

    }

    // Run every hour to save metrics, but metrics are over written on same day


    /**
     *
     * @return The current metrics for Schedule Admin Controller
     */

    public DE_SchedulerMetrics getMetrics(){
        DE_SchedulerMetrics metrics = new DE_SchedulerMetrics();
        metrics.setDate(LocalDateTime.now().minusHours(1).toLocalDate());
        metrics.setFailureCount(failureCount.get());
        metrics.setRunCount(runCount.get());
        metrics.setSecondsNotRunning(secondsNotRunning.get());
        metrics.setSecondsRunning(secondsRunning.get());
        metrics.setSuccessCount(successCount.get());
        metrics.setSecondsQueued(secondsQueued.get());
        metrics.setSecondsQueueEmpty(secondsQueueEmpty.get());
        return metrics;
    }

    /**
     * After restart we wish to reload any existing metrics save in previous hours
     *
     * @param now
     */
    public void initializeMetrics(LocalDate now) {
        try {
            Optional<DE_SchedulerMetrics> metrics = drSchedulerMetrics.allInDateRange(PortalEnum.AWS, now, now).stream().findFirst();
            metrics.ifPresent(x -> {
                failureCount = new AtomicInteger(x.getFailureCount());
                successCount = new AtomicInteger(x.getSuccessCount());
                runCount = new AtomicInteger(x.getRunCount());
                secondsRunning = new AtomicInteger(x.getSecondsRunning());
                secondsNotRunning = new AtomicInteger(x.getSecondsNotRunning());
                secondsQueued = new AtomicInteger(x.getSecondsQueued());
                secondsQueueEmpty = new AtomicInteger(x.getSecondsQueueEmpty());
            });
        } catch (Exception e){
            LOGGER.error("Error initializing metrics");
        }
    }

    /*
     *  Run every second to update metrics
     */
    @Scheduled(cron = "* * * * * ?")
    public void updateMetrics(){
        int q = mpcReportScheduler==null? 0 : mpcReportScheduler.queueSize();
        if (q > MAX_QUEUE_SIZE) {
            maxQueueSize.incrementAndGet();
        }
        if (q == 0) {
            secondsQueueEmpty.incrementAndGet();
        } else {
            secondsQueued.incrementAndGet();
        }

        if (mpcReportScheduler!=null && mpcReportScheduler.getRunningSize()>0) {
            secondsRunning.incrementAndGet();
        } else {
            secondsNotRunning.incrementAndGet();
        }
    }

}
