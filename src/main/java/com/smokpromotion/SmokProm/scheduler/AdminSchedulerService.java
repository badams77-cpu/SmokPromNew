package com.smokpromotion.SmokProm.scheduler;

import com.urcompliant.config.admin.AdminSecurityPrinciple;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_Practice;
import com.urcompliant.domain.service.DS_Practice;
import com.urcompliant.form.ReportCriteriaForm;
import com.urcompliant.form.ReportCriteriaFormSupport;
import com.urcompliant.practicedata.client.EPDClientException;
import com.urcompliant.practicedata.generic.ProviderEntity;
import com.urcompliant.practicedata.generic.ProviderService;
import com.urcompliant.scheduler.dao.DE_SchedulerMetrics;
import com.urcompliant.scheduler.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@Profile("admin")
public class AdminSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSchedulerService.class);

    private final DR_AdminSchedule drSchedule;
    private final DR_AdminSchedulerMetrics drSchedulerMetrics;
    private final DR_AdminSchedulePermissions drSchedulePermissions;
    private static final PortalEnum PORTAL = PortalEnum.AWS;

    @Autowired
    public AdminSchedulerService(DR_AdminSchedule drSchedule,
                                 DR_AdminSchedulerMetrics drSchedulerMetrics,
                                 DR_AdminSchedulePermissions drSchedulePermissions
                            ){
        this.drSchedule = drSchedule;
        this.drSchedulerMetrics = drSchedulerMetrics;
        this.drSchedulePermissions = drSchedulePermissions;
    }

    public LocalDateTime scheduleReport(AdminSecurityPrinciple principle, ReportCriteriaForm form, Class controller, String method) {
        ReportCriteriaFormSupport support = getSupportingData(principle);
        if (drSchedulePermissions.isSchedulingPermitted(principle)) {
            if (form.isProviderSelectionEnabled() && form.getProviderIds(support).size()==support.getProviders().size() ){
                form.setProviderKeys(new LinkedList<>());
            }
            if (form.isPracticeSelectionEnabled() && form.getPracticeIds().size()==support.getUsersPractices().size() ){
                form.setPracticeIds(new LinkedList<>());
            }
            return drSchedule.scheduleReport(PORTAL, principle, form, controller, method);
        } else {
            LOGGER.error("Scheduling Not Permitted for "+principle.getEmail());
            return null;
        }
    }

    public List<DE_SchedulerMetrics> getMetricsForPeriod(LocalDateTime start, LocalDateTime end){
        return drSchedulerMetrics.allInDateRange(PORTAL, start.toLocalDate(), end.toLocalDate());
    }

    public boolean isSchedulingPermitted(AdminSecurityPrinciple principle){
        return drSchedulePermissions.isSchedulingPermitted(principle);
    }

    private ReportCriteriaFormSupport getSupportingData(AdminSecurityPrinciple principle) {

        ReportCriteriaFormSupport support = new ReportCriteriaFormSupport();

        return support;
    }

}

