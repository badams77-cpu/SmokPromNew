package com.smokpromotion.SmokProm.scheduler.dao;

public class DE_ScheduleWithLastLog extends DE_Schedule {

    private String practiceGroupName;

    private DE_ScheduleLogEntry lastLog;

    public DE_ScheduleLogEntry getLastLog() {
        return lastLog;
    }

    public void setLastLog(DE_ScheduleLogEntry lastLog) {
        this.lastLog = lastLog;
    }

    public String getPracticeGroupName() {
        return practiceGroupName;
    }

    public void setPracticeGroupName(String practiceGroupName) {
        this.practiceGroupName = practiceGroupName;
    }

    @Override
    public String toString() {
        return "DE_ScheduleWithLastLog{" +
                super.toString()+
                "practiceGroupName='" + practiceGroupName + '\'' +
                ", lastLog=" + lastLog +
                '}';
    }
}
