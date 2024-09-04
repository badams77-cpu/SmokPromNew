package com.smokpromotion.SmokProm.scheduler;


import com.urcompliant.config.portal.PortalSecurityPrinciple;

import com.urcompliant.domain.EmailLanguage;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_User;

import com.urcompliant.form.ReportCriteriaForm;
import com.urcompliant.practicedata.client.EPDClient;
import com.urcompliant.scheduler.dao.DE_Schedule;
import com.urcompliant.scheduler.dao.DE_ScheduleLogEntry;

import com.urcompliant.scheduler.sevlet.ScheduledHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScheduleTask implements Runnable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(("dd-MM-yy HH:mm:ss"));
    private static final double MILLIS_IN_MINUTE = 60000.0;

    protected static final String MAIL_TEMPLATE = "scheduled_report_success";
    protected static final String FAILURE_TEMPLATE = "scheduled_report_failed";

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    private DE_Schedule report;

    private Map<Integer, ScheduleTask> inProgressMap;

    private final ScheduleMetrics scheduleMetrics;

    private final ScheduleServices scheduleServices;

    private LocalDateTime startDateTime;

    private Thread runner;


    public ScheduleTask(DE_Schedule report, ScheduleServices services, Map<Integer, ScheduleTask> inProgressMap, ScheduleMetrics scheduleMetrics){
        this.report = report;
        this.scheduleServices = services;
        this.scheduleMetrics = scheduleMetrics;
        this.inProgressMap = inProgressMap;
    }



    public void run(){
        runOrFail(report, scheduleServices.adjustTimeToZone(LocalDateTime.now()));
        runner = Thread.currentThread();
    }

    public DE_Schedule getReport(){
        return report;
    }

    public LocalDateTime getStartDateTime(){
        return startDateTime;
    }

    protected void runOrFail(DE_Schedule report, LocalDateTime serverDateTime) {
        this.startDateTime = serverDateTime;
        Optional<DE_Schedule> report1 = scheduleServices.getDrSchedule().getById(PortalEnum.AWS, report.getId());
        LocalDateTime lastrun = report1.map(DE_Schedule::getLastrun).orElse(null);
        if (lastrun != null && lastrun.toLocalDate().isEqual(serverDateTime.toLocalDate() )) {
            LOGGER.warn("runOrFail: Report " + report.getId() + " already has run today");

            return;
        }
        try {
            LOGGER.info("runOrFail: Running Report " + report.getId() + ":" + report.getTheclass());
            scheduleMetrics.incRunCount();
            runReport(report, serverDateTime);
        } catch (Exception e) {

            scheduleMetrics.incFailureCount();
            LOGGER.error("runOrFail: Failed to run report " + report.getId() + ":" + report.getTheclass(), e);
            if (e instanceof SchedulePracticeException){
                LOGGER.error("runOrFail: Report did not have required practices " + report.getId() + ":" + report.getTheclass(), e);
            }
            LocalDateTime errorDateTime = scheduleServices.adjustTimeToZone(LocalDateTime.now());
            DE_ScheduleLogEntry logEntry = new DE_ScheduleLogEntry(report,errorDateTime, e);
            scheduleServices.getDrScheduleLogEntry().saveEntry(PortalEnum.AWS, logEntry);
            scheduleServices.getDrSchedule().setLastStarted(PortalEnum.AWS, report.getId(), report.getStartTime());
            try {
                EmailLanguage language = calculateLanguage(report);
                scheduleServices.getAttachmentMailer().sendTemplate(report.getDestEmail(), FAILURE_TEMPLATE, language, getReplacementMapFailure(report, logEntry));
            } catch (MessagingException e1) {
                LOGGER.error("runOrFail: Failed to email failure message " + report.getId() + ":" + report.getTheclass(), e);
                DE_ScheduleLogEntry logEntry1 = new DE_ScheduleLogEntry(report, errorDateTime, e1);
                scheduleServices.getDrScheduleLogEntry().saveEntry(PortalEnum.AWS, logEntry1);
            } finally {
                scheduleServices.getDrSchedule().setLastRun(PortalEnum.AWS, report.getId(), LocalDateTime.now());
                scheduleServices.getDrSchedule().setLastStarted(PortalEnum.AWS,report.getId(),report.getStartTime());
            }
        } finally {
            inProgressMap.remove(report.getId());
            runner =null;
        }
    }

    public void cancel(){
        if (runner!=null) {
            EPDClient.cancel(runner);
        }
    }

    public int getEstimate() {
        try {
            Object reportObject = scheduleServices.getContext().getBean(report.getTheclass());
            if (reportObject == null) {
                LOGGER.error("getEstimate: Cannot estimate task " + report.getTheclass() + " not found");
                return 0;
            }
            DE_User user = scheduleServices.getUserService().getUser(report.getUsername());
            if (user == null) {
                throw new BadCredentialsException("User: " + report.getUsername() + " doesn't exist");
            }
            if (!user.isUseractive()) {
                throw new BadCredentialsException("User: " + report.getUsername() + " not active");
            }
            PortalSecurityPrinciple principle = scheduleServices.getPrincipleService().create(user,"");
            NamedReport namedReport = (NamedReport) reportObject;
            ReportCriteriaForm form = getReportCriteriaForm(report);
            return namedReport.estimateSecondsToRun(form, PortalEnum.valueOf(report.getPortal()), principle.getPracticeGroupId());
        } catch (Exception e){
            LOGGER.error("getEstimate: Cannot estimate task " + report.getTheclass() + " exception ",e);
            return 0;
        }
    }

    protected void runReport(DE_Schedule report, LocalDateTime serverDateTime) throws Exception {
        report.setStartTime(serverDateTime);
        Object reportObject = scheduleServices.getContext().getBean(report.getTheclass());
        if (reportObject == null) {
            LOGGER.error("runReport: Cannot run task " + report.getTheclass() + " not found");
            throw new ClassNotFoundException("Class not found" + report.getTheclass() + " not found");
        }
        boolean useAES = reportObject instanceof AESNamedReport;
        boolean found = false;
        for (Method method : reportObject.getClass().getDeclaredMethods()) {
            if (method.getName().equals(report.getMethod())) {
                found = true;
                invokeAndSend(reportObject, method, report, useAES);
            }
        }
        if (!found) {
            LOGGER.error("runReport: Class found but method" + report.getMethod() + " not found");
            throw new ClassNotFoundException("Class found but method" + report.getMethod() + " not found");
        }
    }

    private void invokeAndSend(Object reportObject, Method method, DE_Schedule report, boolean useAES) throws Exception {
        DE_User user = scheduleServices.getUserService().getUser(report.getUsername());
        if (user == null || (user.getPracticeGroupID()!=report.getPracticeGroupId() && report.getPracticeGroupId()!=0)) {
            throw new BadCredentialsException("User: " + report.getUsername() + " has incorrect practice group");
        }
        if (!user.isUseractive()) {
            throw new BadCredentialsException("User: " + report.getUsername() + " not active");
        }
        PortalSecurityPrinciple principle = scheduleServices.getPrincipleService().create(user,"");
        if (principle==null || !scheduleServices.getDrSchedulePermissions().isSchedulingPermitted(principle)) throw new BadCredentialsException("User: "+report.getUsername()+" is not permitted to schedule reports");
        Class[] parameterTypes = method.getParameterTypes();
        Parameter[] parameter = method.getParameters();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object args[] = new Object[parameterTypes.length];
        ScheduledHttpServletResponse response = new ScheduledHttpServletResponse();
        ReportCriteriaForm form = getReportCriteriaForm(report);
        if (form.getPracticeIds()!=null && !form.getPracticeIds().isEmpty()){
            if (!form.getPracticeIds().stream().anyMatch(p->principle.userHasPractice(p))){
                scheduleServices.getDrSchedule().delete(principle.getPortal(),report.getId(), report.getUsername());
                throw new SchedulePracticeException("User: " + report.getUsername() + " does not have access to any of the specified practices");
            }
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = generateParameter(parameterTypes[i], parameter[i], annotations[i], report, form, response, principle);
        }
        method.invoke(reportObject, args);
        byte[] file = response.closeAndGet();
        byte[] zip = scheduleServices.getZipHelper().zipFileProtected(file, response.getFilename(), scheduleServices.getCryptoService().decrypt(report.getZipPass()),useAES);

        List<String> destinations = new LinkedList<>();
        if (report.getRecipients()!=null && !report.getRecipients().equals("")){
            String[] recipients = report.getRecipients().split(",");
            Set<String> groupEmails = scheduleServices.getUserService().getByPracticeGroup(principle.getPortal(), principle.getPracticeGroupId()).stream().map(x->x.getUsername().toLowerCase()).collect(Collectors.toSet());
            for(String recip : recipients){
                String reci1 = recip.trim();
                if (groupEmails.contains(reci1.toLowerCase())){
                    destinations.add(reci1);
                } else {
                    LOGGER.warn("invokeAndSend: recipient: "+reci1+" is not a user in the group");
                }
            }
        } else {
            destinations.add(report.getDestEmail());
        }
        for(String to : destinations) {
            scheduleServices.getAttachmentMailer().sendAttachmentTemplate(to, zip, response.getFilename().replace("csv", "zip"), MAIL_TEMPLATE,
                    calculateLanguage(report), getReplacementMap(report));
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime serverDateTime = scheduleServices.adjustTimeToZone(now);
        scheduleServices.getDrSchedule().setLastRun(PortalEnum.AWS, report.getId(), serverDateTime);
        scheduleServices.getDrScheduleLogEntry().saveEntry(PortalEnum.AWS, new DE_ScheduleLogEntry(report, zip, serverDateTime));
        scheduleServices.getDrSchedule().setLastStarted(PortalEnum.AWS, report.getId(), report.getStartTime());
        scheduleMetrics.incSuccessCount();
    }

    private Map<String, String> getReplacementMap(DE_Schedule report) {
        Map<String, String> map = new HashMap<>();
        map.put("reportName", report.getReportname());
        map.put("startTime", report.getStartTime().format(DATE_FORMAT));
        return map;
    }

    private Map<String, String> getReplacementMapFailure(DE_Schedule report, DE_ScheduleLogEntry log) {
        Map<String, String> map = new HashMap<>();
        map.put("reportName", report.getReportname());
        map.put("startTime", report.getStartTime().format(DATE_FORMAT));
        map.put("runtime", String.format("%,.3f", log.getRuntime() / MILLIS_IN_MINUTE));
        if (report.getInterruptReason()!=null) {
            map.put("interruptReason", report.getInterruptReason());
        }
        return map;

    }

    private EmailLanguage calculateLanguage(DE_Schedule schedule) {
        DE_User user = scheduleServices.getUserService().getUser(schedule.getUsername());
        if (user==null ){
            return EmailLanguage.UNKNOWN;
        }
        if (user.getLanguageSetting()==null){
            return EmailLanguage.UNKNOWN;
        }
        return user.getLanguageSetting().getEmailLanguage();
    }




    /*
                HttpServletRequest request,
            Authentication authentication,
            Model model,
            @PathVariable String format,
            @Valid ReportCriteriaForm reportCriteriaForm,
            BindingResult bindingResult,
            HttpServletResponse response)
     */

    private Object generateParameter(Class param,
                                     Parameter parameter, Annotation[] annotations,
                                     DE_Schedule report, ReportCriteriaForm form,
                                     ScheduledHttpServletResponse response,
                                     PortalSecurityPrinciple principle)
            throws Exception {
        switch (param.getName()) {
            case "javax.servlet.http.HttpServletRequest":
                return getHttpServletRequest();
            case "javax.servlet.http.HttpServletResponse":
                return response;
            case "org.springframework.security.core.Authentication":
                return getAuthentication(principle);
            case "org.springframework.validation.BindingResult":
                return validBindingResult();
            case "java.lang.String":
                return getString(parameter, annotations);
            case "org.springframework.ui.Model":
                return mock(Model.class);
            case "com.urcompliant.form.ReportCriteriaForm":
                return form;
            default:
                return null;
        }
    }

    private ReportCriteriaForm getReportCriteriaForm(DE_Schedule report) throws Exception {
        ReportCriteriaForm form = scheduleServices.getMapper().readValue(report.getReportCriteriaForm(), ReportCriteriaForm.class);
        switch (report.getSchedule()) {
            case NOW:
                return form;
            case DAILY:
                form.setAsOfDate((LocalDate.now().minusDays(1)));
                form.setEndDate(LocalDate.now().minusDays(1));
                form.setStartDate(LocalDate.now().minus(1, ChronoUnit.DAYS));
                return form;
            case WEEKLY:
                form.setAsOfDate((LocalDate.now().minusDays(1)));
                form.setEndDate(LocalDate.now().minusDays(1));
                form.setStartDate(LocalDate.now().minus(7, ChronoUnit.DAYS));
                return form;
            case MONTHLY:
                form.setAsOfDate((LocalDate.now().minusDays(1)));
                form.setEndDate(LocalDate.now().minusDays(1));
                form.setStartDate(LocalDate.now().minus(1, ChronoUnit.MONTHS));
                return form;
            case YEARLY:
                form.setAsOfDate((LocalDate.now().minusDays(1)));
                form.setEndDate(LocalDate.now().minusDays(1));
                form.setStartDate(LocalDate.now().minus(1, ChronoUnit.YEARS));
                return form;
            default:
                form.setAsOfDate((LocalDate.now().minusDays(1)));
                return form;
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        HttpServletRequest mock = mock(HttpServletRequest.class);
        return mock;
    }

    private Authentication getAuthentication(PortalSecurityPrinciple principle) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principle);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }

    private BindingResult validBindingResult() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        return result;
    }

    private String getString(Parameter param, Annotation[] annotations) {
        PathVariable[] ann = param.getAnnotationsByType(PathVariable.class);
        if (Arrays.stream(ann).anyMatch(
                x -> x.name().equals("format")
        )) {
            return "csv";
        }
        return "";
    }

}
