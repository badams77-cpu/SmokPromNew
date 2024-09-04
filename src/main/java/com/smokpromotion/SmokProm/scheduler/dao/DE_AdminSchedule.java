package com.smokpromotion.SmokProm.scheduler.dao;

import com.urcompliant.config.portal.PortalSecurityPrinciple;

public class DE_AdminSchedule {

    private final DE_ScheduleWithLastLog scheduleWithLastLog;

    private final PortalSecurityPrinciple principle;

    public DE_AdminSchedule(PortalSecurityPrinciple principle, DE_ScheduleWithLastLog schedule) {
        this.scheduleWithLastLog = schedule;
        this.principle = principle;
    }

    public DE_ScheduleWithLastLog getSchedule() {
        return scheduleWithLastLog;
    }

    public PortalSecurityPrinciple getPrinciple() {
        return principle;
    }
}

