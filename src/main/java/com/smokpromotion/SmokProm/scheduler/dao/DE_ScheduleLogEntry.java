package com.smokpromotion.SmokProm.scheduler.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DE_ScheduleLogEntry {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
    public static final double MILLIS_IN_MINUTE = 60000.0;
    public static final double BYTES_IN_KB = 1024.0;

    int id;
    int scheduleId;
    LocalDateTime startTime;
    long runtime;
    StatusEnum status;
    long attachmentSize;
    String exceptionClass;
    String exceptionMessage;
    String exceptionStackTrace;
    String causeClass="";
    String causeMessage="";
    String causeStackTrace="";
    public DE_ScheduleLogEntry(){

    }

    public DE_ScheduleLogEntry(DE_Schedule report, LocalDateTime now, Throwable e) {
        scheduleId = report.getId();
        status = StatusEnum.FAILED;
        startTime = report.getStartTime();
        runtime = Duration.between( startTime,now).toMillis();
        if (e instanceof java.lang.reflect.InvocationTargetException && e.getCause()!=null){
            e = e.getCause();
        }
        exceptionClass = e.getClass().getName();
        exceptionMessage = report.getInterruptReason()==null ?  e.getMessage() : report.getInterruptReason();
        exceptionStackTrace = readStackTrace(e);
        Throwable cause = e.getCause();
        if (cause!=null){
            causeClass = cause.getClass().getName();
            causeMessage = report.getInterruptReason()==null ?  cause.getMessage() : report.getInterruptReason();
            causeStackTrace = readStackTrace(cause);
        }
        attachmentSize = 0;
    }

    public DE_ScheduleLogEntry(DE_Schedule report, byte[] zipFile, LocalDateTime now){
        scheduleId = report.getId();
        status = StatusEnum.SUCCESS;
        startTime = report.getStartTime();
        runtime = Duration.between( startTime, now).toMillis();
        attachmentSize = zipFile.length;
        exceptionClass = "";
        exceptionMessage = "";
        exceptionStackTrace = "";
    }

    private String readStackTrace(Throwable e){
        try (StringWriter strWriter = new StringWriter(); PrintWriter writer = new PrintWriter(strWriter) ){
            e.printStackTrace(writer);
            writer.close();
            strWriter.close();
            return strWriter.toString();
        } catch (IOException e1){
            return "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeFormatted(){
        return startTime==null?"" : getDateFormatted(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getRuntime() {
        return runtime;
    }

    public String getRuntimeFormatted() {
        return formatDecimal(runtime/ MILLIS_IN_MINUTE);
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public String getStatusFormatted(){
        return status==StatusEnum.UNKNOWN? "No Log Entry": status.name();
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public long getAttachmentSize() {
        return attachmentSize;
    }

    public String getAttachmentSizeFormatted() {
        return formatDecimal(attachmentSize/ BYTES_IN_KB);
    }

    public void setAttachmentSize(long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

    public void setExceptionStackTrace(String exceptionStackTrace) {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public String getCauseClass() {
        return causeClass;
    }

    public void setCauseClass(String causeClass) {
        this.causeClass = causeClass;
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public String getCauseMessageTruncated(int length) {
        if (causeMessage==null) return "";
        if (causeMessage.length()>length){ return causeMessage.substring(0,length); }
        return causeMessage;
    }

    public void setCauseMessage(String causeMessage) {
        this.causeMessage = causeMessage;
    }

    public String getCauseStackTrace() {
        return causeStackTrace;
    }

    public void setCauseStackTrace(String causeStackTrace) {
        this.causeStackTrace = causeStackTrace;
    }

    private String getDateFormatted(LocalDateTime dateToFormat) {
        String dateStr = "";
        if (dateToFormat != null) {
            dateStr = dateToFormat.format(FORMAT);
        }
        return dateStr;
    }

    private String formatNumber(double input){
        return String.format("%,.0f", input);
    }

    private String formatDecimal(double input){
        return String.format("%,.1f", input);
    }
}
