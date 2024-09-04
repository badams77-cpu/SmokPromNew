package com.smokpromotion.SmokProm.scheduler;

import com.urcompliant.scheduler.dao.DE_Schedule;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service get the reporrt name for the given Named Report using Spring getBean
 */
@Service
public class ScheduleClassInfo {

    private final ApplicationContext context;

    @Autowired
    public ScheduleClassInfo(ApplicationContext context){
        this.context = context;
    }

    public String getReportName(DE_Schedule report){
        try {
            Object reportController = context.getBean(report.getTheclass());
            if (reportController == null) {
                return "Not present";
            }
            if (reportController instanceof NamedReport) {
                return ((NamedReport) reportController).getReportName();
            }
            return "Unnamed";
        } catch (NoSuchBeanDefinitionException e){
            return "Report Not Found";
        }
    }

}
