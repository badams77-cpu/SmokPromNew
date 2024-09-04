package com.smokpromotion.SmokProm.scheduler.dao;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Objects;

/**
 * Table AWS schedule_rpt
 * Represent class,method to run, reportCriteriaForm and startDate setting
 */
public class DE_Schedule {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

    private int id;
    private int practiceGroupId;
    private String portal;
    private boolean active;
    private String username;
    private String reportname;
    private String firstname;
    private String lastname;
    private String destEmail;
    private String zipPass;
    private ScheduleEnum schedule;
    private int dayOfWeek;
    private int dayOfMonth;
    private int hourOfDay;
    private int monthOfYear;
    private int minutesOfHour;
    private LocalDateTime lastrun;
    private String theclass;
    private String method;

    private String recipients;
    private String reportCriteriaForm;
    private LocalDateTime startTime;
    private int startDateDaysAgo;
    private boolean deleted;
    private LocalDateTime createdDateTime;
    private String interruptReason;

    public String getZipPass() {
        return zipPass;
    }

    public void setZipPass(String zipPass) {
        this.zipPass = zipPass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReportname() {
        return reportname;
    }

    public void setReportname(String reportname) {
        this.reportname = reportname;
    }

    public String getDestEmail() {
        return destEmail;
    }

    public void setDestEmail(String destEmail) {
        this.destEmail = destEmail;
    }

    public ScheduleEnum getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleEnum schedule) {
        this.schedule = schedule;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public int getMinutesOfHour() {
        return minutesOfHour;
    }

    public void setMinutesOfHour(int minutes_of_hour) {
        this.minutesOfHour = minutes_of_hour;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public LocalDateTime getLastrun() {
        return lastrun;
    }

    public void setLastrun(LocalDateTime lastrun) {
        this.lastrun = lastrun;
    }

    public String getTheclass() {
        return theclass;
    }

    public void setTheclass(String theclass) {
        this.theclass = theclass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStartDateDaysAgo() {
        return startDateDaysAgo;
    }

    public void setStartDateDaysAgo(int startDateDaysAgo) {
        this.startDateDaysAgo = startDateDaysAgo;
    }

    public String getReportCriteriaForm() {
        return reportCriteriaForm;
    }

    public void setReportCriteriaForm(String reportCriteriaForm) {
        this.reportCriteriaForm = reportCriteriaForm;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getCurrentDurationMins() {
        if (startTime != null) {
            return startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
        }
        return 0;
    }

    public String getStartTimeFormatted(){
        return startTime==null? "" : startTime.format(FORMAT);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getInterruptReason() {
        return interruptReason;
    }

    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    public int getPracticeGroupId() {
        return practiceGroupId;
    }

    public void setPracticeGroupId(int practiceGroupId) {
        this.practiceGroupId = practiceGroupId;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DE_Schedule)) return false;
        DE_Schedule that = (DE_Schedule) o;
        return getId() == that.getId() && getPracticeGroupId() == that.getPracticeGroupId() && isActive() == that.isActive() && getDayOfWeek() == that.getDayOfWeek() && getDayOfMonth() == that.getDayOfMonth() && getHourOfDay() == that.getHourOfDay() && getMonthOfYear() == that.getMonthOfYear() && getMinutesOfHour() == that.getMinutesOfHour() && getStartDateDaysAgo() == that.getStartDateDaysAgo() && isDeleted() == that.isDeleted() && Objects.equals(getPortal(), that.getPortal()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getReportname(), that.getReportname()) && Objects.equals(getFirstname(), that.getFirstname()) && Objects.equals(getLastname(), that.getLastname()) && Objects.equals(getDestEmail(), that.getDestEmail()) && Objects.equals(getZipPass(), that.getZipPass()) && getSchedule() == that.getSchedule() && Objects.equals(getLastrun(), that.getLastrun()) && Objects.equals(getTheclass(), that.getTheclass()) && Objects.equals(getMethod(), that.getMethod()) && Objects.equals(getRecipients(), that.getRecipients()) && Objects.equals(getReportCriteriaForm(), that.getReportCriteriaForm()) && Objects.equals(getStartTime(), that.getStartTime()) && Objects.equals(getCreatedDateTime(), that.getCreatedDateTime()) && Objects.equals(getInterruptReason(), that.getInterruptReason());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPracticeGroupId(), getPortal(), isActive(), getUsername(), getReportname(), getFirstname(), getLastname(), getDestEmail(), getZipPass(), getSchedule(), getDayOfWeek(), getDayOfMonth(), getHourOfDay(), getMonthOfYear(), getMinutesOfHour(), getLastrun(), getTheclass(), getMethod(), getRecipients(), getReportCriteriaForm(), getStartTime(), getStartDateDaysAgo(), isDeleted(), getCreatedDateTime(), getInterruptReason());
    }

    public LocalDateTime getNextRun(LocalDateTime time){
        LocalDate today = time.toLocalDate();
        boolean runToday = lastrun!=null && lastrun.toLocalDate().isEqual(today);
        switch ((schedule)){
            case NOW :
                if (lastrun!=null){ return null; }
                return time;
            case DAILY :
                LocalDateTime then =  time
                        .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                        .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                        .with(ChronoField.SECOND_OF_MINUTE,0)
                        .with(ChronoField.NANO_OF_SECOND,0);
                if ( (then.isBefore(time) &&  !runToday && !isSameDay(then,time))  ||
                        (then.isBefore(time.now()) && runToday && isSameDay(then,time)) ||
                        then.isBefore(createdDateTime)){
                    return then.plusDays(1);
                } else {
                    return then;
                }
            case WEEKLY :
                LocalDateTime then1 = time
                        .with(ChronoField.DAY_OF_WEEK, dayOfWeek)
                        .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                        .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                        .with(ChronoField.SECOND_OF_MINUTE,0)
                        .with(ChronoField.NANO_OF_SECOND,0);
                if ( (then1.isBefore(time) && !runToday && !isSameDay(then1, time)) ||
                        (then1.isBefore(time.now()) && runToday && isSameDay(then1,time)) ||
                        then1.isBefore(createdDateTime)){
                    return then1.plusDays(7);
                } else {
                    return then1;
                }
            case MONTHLY:
                LocalDateTime then2 = time;
                try {
                    then2 = time
                            .with(ChronoField.DAY_OF_MONTH, dayOfMonth)
                            .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                            .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                            .with(ChronoField.SECOND_OF_MINUTE, 0)
                            .with(ChronoField.NANO_OF_SECOND, 0);
                } catch (DateTimeException e){ // Handle 29,30,31 Feburary skip to next month;
                    then2 =time
                            .plusMonths(1)
                            .with(ChronoField.DAY_OF_MONTH, dayOfMonth)
                            .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                            .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                            .with(ChronoField.SECOND_OF_MINUTE, 0)
                            .with(ChronoField.NANO_OF_SECOND, 0);
                }
                if ( (then2.isBefore(time) && !runToday && !isSameDay(then2, time)) ||
                        (then2.isBefore(time.now()) && runToday && isSameDay(then2,time)) ||
                        then2.isBefore(createdDateTime)){
                    return then2.plus(1, ChronoUnit.MONTHS);
                } else {
                    return then2;
                }
            case YEARLY:
                    LocalDateTime then3 = time;
                try {
                     then3 = time
                            .with(ChronoField.MONTH_OF_YEAR, monthOfYear)
                            .with(ChronoField.DAY_OF_MONTH, dayOfMonth)
                            .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                            .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                            .with(ChronoField.SECOND_OF_MINUTE, 0)
                            .with(ChronoField.NANO_OF_SECOND, 0);
                } catch (Exception e) {
                    if (monthOfYear==2 && dayOfMonth==29){
                        int year = time.getYear();
                        while(!isLeapYear(year)){
                            year++;
                        }
                        then3 = time.withYear(year)
                                .with(ChronoField.MONTH_OF_YEAR, monthOfYear)
                                .with(ChronoField.DAY_OF_MONTH, dayOfMonth)
                                .with(ChronoField.HOUR_OF_DAY, hourOfDay)
                                .with(ChronoField.MINUTE_OF_HOUR, minutesOfHour)
                                .with(ChronoField.SECOND_OF_MINUTE, 0)
                                .with(ChronoField.NANO_OF_SECOND, 0);
                    } else {
                        then3=LocalDateTime.MAX;
                    }
                }
                if ( (then3.isBefore(time) && !runToday && !isSameDay(then3,time)) ||
                        (then3.isBefore(time.now()) && runToday && isSameDay(then3,time)) ||
                        then3.isBefore(createdDateTime)){
                    return then3.plus(1,ChronoUnit.YEARS);
                } else {
                    return then3;
                }
            default:
                return null;
        }
    }

    private boolean isSameDay(LocalDateTime day1, LocalDateTime day2){
        return day1.toLocalDate().isEqual(day2.toLocalDate());
    }


    public String getNextRunFormatted(){
        LocalDateTime next = getNextRun(LocalDateTime.now());
        if (next==null){ return "Never Again"; }
        return next.format(FORMAT);
    }

    public String getCreatedFormatted(){
        return createdDateTime.format(FORMAT);
    }

    public String getLastRunFormatted() {
        if (lastrun == null) {
            return "Never";
        }
        return lastrun.format(FORMAT);
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    @Override
    public String toString() {
        return "DE_Schedule{" +
                "id=" + id +
                ", practiceGroupId=" + practiceGroupId +
                ", portal='" + portal + '\'' +
                ", active=" + active +
                ", username='" + username + '\'' +
                ", reportname='" + reportname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", destEmail='" + destEmail + '\'' +
                ", zipPass='********'" +
                ", schedule=" + schedule +
                ", dayOfWeek=" + dayOfWeek +
                ", dayOfMonth=" + dayOfMonth +
                ", hourOfDay=" + hourOfDay +
                ", monthOfYear=" + monthOfYear +
                ", minutesOfHour=" + minutesOfHour +
                ", lastrun=" + lastrun +
                ", theclass='" + theclass + '\'' +
                ", method='" + method + '\'' +
                ", receipents='" + recipients + '\'' +
                ", reportCriteriaForm='" + reportCriteriaForm + '\'' +
                ", startTime=" + startTime +
                ", startDateDaysAgo=" + startDateDaysAgo +
                ", deleted=" + deleted +
                ", createdDateTime=" + createdDateTime +
                ", interruptReason='" + interruptReason + '\'' +
                '}';
    }
}
