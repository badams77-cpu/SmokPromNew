package com.smokpromotion.SmokProm.scheduler;

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
import com.urcompliant.scheduler.repository.DR_Schedule;
import com.urcompliant.scheduler.repository.DR_SchedulePermissions;
import com.urcompliant.scheduler.repository.DR_SchedulerMetrics;
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
public class SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private final DR_Schedule drSchedule;
    private final DR_SchedulerMetrics drSchedulerMetrics;
    private final DR_SchedulePermissions drSchedulePermissions;
    private static final PortalEnum PORTAL = PortalEnum.AWS;
    private ProviderService providerService;
    private DS_Practice practiceService;

    @Autowired
    public SchedulerService(DR_Schedule drSchedule,
                            DR_SchedulerMetrics drSchedulerMetrics,
                            DR_SchedulePermissions drSchedulePermissions,
                            DS_Practice practiceService,
                            ProviderService providerService
                            ){
        this.drSchedule = drSchedule;
        this.drSchedulerMetrics = drSchedulerMetrics;
        this.drSchedulePermissions = drSchedulePermissions;
        this.providerService = providerService;
        this.practiceService = practiceService;
    }

    public LocalDateTime scheduleReport( PortalSecurityPrinciple principle, ReportCriteriaForm form, Class controller, String method) {
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

    public boolean isSchedulingPermitted(PortalSecurityPrinciple principle){
        return drSchedulePermissions.isSchedulingPermitted(principle);
    }

    private ReportCriteriaFormSupport getSupportingData(PortalSecurityPrinciple principle) {

        ReportCriteriaFormSupport support = new ReportCriteriaFormSupport();

        Set<String> practiceCodeSet = new HashSet<>();
        for(DE_Practice practice : practiceService.getForPrinciple()) {
            support.addUserPractice(practice.getId(), practice.getPracticeName(), practice.getPracticeCode());
            practiceCodeSet.add(practice.getPracticeCode());
        }
        try {
            List<ProviderEntity> providers = providerService.getAll(
                    principle.getSqlServer(), principle.getGroupDb(), practiceCodeSet, null);

            support.setProviders(providers);
        } catch (EPDClientException e){
            LOGGER.error("Couldn't set providers, EPDR exception in ProviderService.getALL",e);
            support.setProviders(new LinkedList<>());
        }
        return support;

    }

}

