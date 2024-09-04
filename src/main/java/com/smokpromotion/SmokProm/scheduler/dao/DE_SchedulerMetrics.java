package com.smokpromotion.SmokProm.scheduler.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DE_SchedulerMetrics {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy");



    private LocalDate date;
    private int runCount;
    private int failureCount;
    private int successCount;
    private int secondsRunning;
    private int secondsNotRunning;
    private int secondsQueued;
    private int secondsQueueEmpty;

    public LocalDate getDate() {
        return date;
    }
    public String getDateFormatted() {
        return date.format(FORMAT);
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getSecondsRunning() {
        return secondsRunning;
    }

    public void setSecondsRunning(int secondsRunning) {
        this.secondsRunning = secondsRunning;
    }

    public int getSecondsNotRunning() {
        return secondsNotRunning;
    }

    public void setSecondsNotRunning(int secondsNotRunning) {
        this.secondsNotRunning = secondsNotRunning;
    }

    public int getSecondsQueued() {
        return secondsQueued;
    }

    public void setSecondsQueued(int secondsQueued) {
        this.secondsQueued = secondsQueued;
    }

    public int getSecondsQueueEmpty() {
        return secondsQueueEmpty;
    }

    public void setSecondsQueueEmpty(int secondsQueueEmpty) {
        this.secondsQueueEmpty = secondsQueueEmpty;
    }
}
