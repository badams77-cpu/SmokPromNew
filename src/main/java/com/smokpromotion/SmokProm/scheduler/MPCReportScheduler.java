package com.smokpromotion.SmokProm.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urcompliant.config.portal.PortalSecurityPrincipleService;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.form.ReportCriteriaForm;
import com.urcompliant.practicedata.client.EPDClient;
import com.urcompliant.form.ReportCriteriaFormSupportHourOfDay;
import com.urcompliant.scheduler.dao.DE_Schedule;

import com.urcompliant.scheduler.repository.DR_Schedule;
import com.urcompliant.scheduler.repository.DR_ScheduleLogEntry;
import com.urcompliant.scheduler.repository.DR_SchedulePermissions;
import com.urcompliant.scheduler.service.CryptoService;
import com.urcompliant.service.SmtpMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service
@Profile({"portal","admin"})
public class MPCReportScheduler implements Runnable {



    private static final int DAYS_IN_WEEK = 7;
    private static final int MIN_DAYS_IN_MONTH = 28;
    private static final int DAYS_IN_YEAR = 365;

    private static final Logger LOGGER = LoggerFactory.getLogger(MPCReportScheduler.class);




    private boolean running;
    private final ConcurrentLinkedQueue<DE_Schedule> queue;
    private final Map<Integer, ScheduleTask> inProgressMap;
    private Object lock = new Object();
    private Thread runner;

    private final long timeout;
    private String timezone;
    private MPCAppDBConnectionFactory dbFactory;
    private final ScheduleMetrics scheduleMetrics;

    private final ThreadPoolExecutor slowThreadPool;
    private final ThreadPoolExecutor fastThreadPool;
    private final int estimateCutOffForFast;

    private ScheduleServices scheduleServices;

    @Autowired
    public MPCReportScheduler(DR_Schedule drSchedule,
                       DR_ScheduleLogEntry drScheduleLogEntry,
                       ScheduleMetrics scheduleMetrics,
                       DR_SchedulePermissions drSchedulePermissions,
                       ApplicationContext context,
                       PortalSecurityPrincipleService principleService,
                       DS_UserService userService,
                       ObjectMapper mapper,
                       SmtpMailSender attachmentMailer,
                       ZipHelper zipHelper,
                       CryptoService cryptoService,
                       @Value("${MPC_SCHEDULER_RUN:false}") String runScheduler,
                       @Value("${MPC_SCHEDULER_MAX_RUNTIME:1800}") int timeout,
                       @Value("${MPC_SCHEDULER_TIMEZONE:UTC}") String timezone,
                       @Value("${MPC_SCHEDULER_SLOW_MAX_THREADS:1}") int slowPoolSize,
                       @Value("${MPC_SCHEDULER_FAST_MAX_THREADS:1}") int fastPoolSize,
                       @Value("${MPC_SCHEDULER_CUTOFF_FOR_FAST_SECONDS:300}") int estimateCutOffForFast,
                       MPCAppDBConnectionFactory dbFactory
    ) {

        this.running = false;
        this.queue = new ConcurrentLinkedQueue<DE_Schedule>();
        this.inProgressMap = new HashMap<Integer,ScheduleTask>();
        this.timeout = timeout;
        this.timezone = timezone;
        this.dbFactory = dbFactory;
        this.estimateCutOffForFast = estimateCutOffForFast;
        this.scheduleMetrics = scheduleMetrics;
        try {
            ZoneId.of(timezone);
        } catch (Exception e){
            throw new RuntimeException("MPC_SCHEDULER_TIMEZONE not a legal timezone ",e);
        }
        scheduleServices = new ScheduleServices(drSchedule, drScheduleLogEntry, timezone, dbFactory, drSchedulePermissions, context, principleService
                                ,userService, mapper, attachmentMailer, cryptoService, zipHelper);
        slowThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(slowPoolSize);
        fastThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(fastPoolSize);
        String contextStrategy = System.getProperty(SecurityContextHolder.SYSTEM_PROPERTY);
        if (contextStrategy != null && contextStrategy.equals(SecurityContextHolder.MODE_GLOBAL)) {
            LOGGER.error("Can't start report scheduler spring context strategy is GLOBAL");
        } else {
            if (runScheduler != null && Boolean.valueOf(runScheduler)) {
                runner = new Thread(this);
                runner.setDaemon(true);
                running = true;
                runner.start();
            }
        }

    }

    // Run ten seconds
    @Scheduled(cron = "0,10,20,30,40,50 * * * * ?")
    public void scheduledReport() {
    if (!running){ return; }
        LocalDateTime nowInZone = scheduleServices.adjustTimeToZone(LocalDateTime.now());
        if (nowInZone.getHour() < ReportCriteriaFormSupportHourOfDay.SCHEDULER_START_HOUR || nowInZone.getHour() >= ReportCriteriaFormSupportHourOfDay.SCHEDULER_END_HOUR) {
            return;
        }
        List<DE_Schedule> reportsToRun = scheduleServices.getDrSchedule().allActiveNotRunInLastHour(PortalEnum.AWS).stream().filter(x -> scheduleFilter(x, nowInZone)).collect(Collectors.toList());
        for (DE_Schedule report : reportsToRun) {
            synchronized (lock) {
                if (!queue.contains(report) && inProgressMap.get(report.getId()) ==null) {
                    LOGGER.info("Queue to run report " + report.getId() + ":" + report.getTheclass());
                    queue.offer(report);
                }
            }
        }
    }


// Run every minute to ten seconds timeout
    @Scheduled(cron = "1,11,21,31,41,51 * * * * ?")
    public void timeout() {
        for(ScheduleTask schedule : inProgressMap.values()) {
            LocalDateTime startTime = schedule.getStartDateTime();
            if (startTime == null) {
                return; // Job not started yet
            }
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            if (duration.getSeconds() > timeout) {
                if (inProgressMap.containsValue(schedule)) {
                    return; // In case job changes while method ran
                }
                schedule.cancel();
                LOGGER.info("timeout: Job " + schedule.getReport().getReportname() + " interrupted as it took longer than " + timeout + " seconds");
                schedule.getReport().setInterruptReason("Job Interrupted as it took longer than " + timeout + " seconds");
            }
        }
    }






    /* When used by ScheduleController report times without switching timezone
     *
     */

    public boolean scheduleFilter(DE_Schedule report, LocalDateTime now) {
        // The run datetime in the Users expected timezone which is MPC_SCHEDULE_DATETIME
        // Adjust Server Time to MPC_SCHEDULE_TIMEZONE
        //

        LocalDateTime serverDateTime = now;
        LocalDateTime runDateTime = report.getNextRun(serverDateTime);
        // The created datatime is using database and spring timezone, so need to be adjusted to UI timezone
        LocalDateTime createdDateTime = scheduleServices.adjustDBTimeToZone(report.getCreatedDateTime());
        switch (report.getSchedule()) {

            case NOW:
                return report.getLastrun() == null;
            case DAILY:
                if (runDateTime.isAfter(createdDateTime)) {
                    if (report.getLastrun() == null || !report.getLastrun().toLocalDate().equals(serverDateTime.toLocalDate())) {
                        if (runDateTime.isBefore(serverDateTime) || runDateTime.isEqual(serverDateTime)) {
                            return true;
                        }
                    }
                }
                return false;
            case WEEKLY:
                if (runDateTime.isAfter(createdDateTime)) {
                    if (report.getLastrun() == null || !report.getLastrun().toLocalDate().isAfter(serverDateTime.toLocalDate().minusDays(DAYS_IN_WEEK - 1))) {
                        if (runDateTime.isBefore(serverDateTime) || runDateTime.isEqual(serverDateTime)) {
                            return true;
                        }
                    }
                }
                return false;
            case MONTHLY:
                if (runDateTime.isAfter(createdDateTime)) {
                    if (report.getLastrun() == null || !report.getLastrun().toLocalDate().isAfter(serverDateTime.toLocalDate().minusDays(MIN_DAYS_IN_MONTH - 1))) {
                        if (runDateTime.isBefore(serverDateTime) || runDateTime.isEqual(serverDateTime)) {
                            return true;
                        }
                    }
                }
            case YEARLY:
                if (runDateTime.isAfter(createdDateTime)) {
                    if (report.getLastrun() == null || !report.getLastrun().toLocalDate().isAfter(serverDateTime.toLocalDate().minusDays(DAYS_IN_YEAR - 1))) {
                        if (runDateTime.isBefore(serverDateTime) || runDateTime.isEqual(serverDateTime)) {
                            return true;
                        }
                    }
                }
            default:
                return false;
        }
    }

    public int queueSize() {
        return queue.size();
    }





    public boolean isRunning(){
        return !inProgressMap.isEmpty();
    }

    public void shutdown(){
        running = false;
        slowThreadPool.shutdown();
        fastThreadPool.shutdown();
    }

    // used for unit testing
    public void setRunning(boolean isRunning){
        running = isRunning;
    }

    public boolean interrupt(int id ){
        return EPDClient.cancel(runner);
    }

    public List<DE_Schedule> getInProgress(){
        return inProgressMap.values().stream().map(x->x.getReport()).collect(Collectors.toList());
    }

    public Map<Integer, ScheduleTask> getInProgressMap(){
        return inProgressMap;
    }

    public void run() {
        DE_Schedule report;
        scheduleMetrics.setMpcReportScheduler(this);
        LOOP:
        while (running) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime serverDateTime = scheduleServices.adjustTimeToZone(now);
            if (serverDateTime.getHour() < ReportCriteriaFormSupportHourOfDay.SCHEDULER_START_HOUR || serverDateTime.getHour() >= ReportCriteriaFormSupportHourOfDay.SCHEDULER_END_HOUR) {
                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException e) {
                }
                continue LOOP;
            }
            report = queue.peek();
            if (report == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                continue LOOP;
            }
            synchronized (lock) {
                report = queue.poll();
                if (inProgressMap.containsKey(report.getId())){ continue LOOP; }
                ScheduleTask task = new ScheduleTask(report, scheduleServices,inProgressMap, scheduleMetrics);
                inProgressMap.put(report.getId(), task);
                int estimate=  task.getEstimate();
                if (estimate>estimateCutOffForFast){
                    LOGGER.warn("Job: "+report.getId()+ " "+report.getTheclass()+" estimate: "+estimate+" seconds, so "+" Slow Queue");
                    slowThreadPool.execute(task);
                } else {
                    LOGGER.warn("Job: "+report.getId()+ " "+report.getTheclass()+" estimate: "+estimate+" seconds, so "+" Fast Queue");
                    fastThreadPool.execute(task);
                }
            }

        }
        LOGGER.debug("Scheduling Queue Finished");
    }


    public int getQueueSize(){
        return queue.size();
    }

    public int getRunningSize(){
        return slowThreadPool.getActiveCount()+fastThreadPool.getActiveCount();
    }



}
