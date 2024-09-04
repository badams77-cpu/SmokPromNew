package com.smokpromotion.SmokProm.scheduler;

import com.urcompliant.domain.PortalEnum;
import com.urcompliant.form.ReportCriteriaForm;

/**
 * This interface provides a name to report, for the Schuduler system, only needed for schedule reports
 */
public interface NamedReport {

    String getReportName();

    int estimateSecondsToRun(ReportCriteriaForm form, PortalEnum portal, int practiceGroupId);

}
