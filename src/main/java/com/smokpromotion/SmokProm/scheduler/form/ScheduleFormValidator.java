package com.smokpromotion.SmokProm.scheduler.form;

import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.form.ReportCriteriaForm;
import com.urcompliant.form.ReportCriteriaFormSupportHourOfDay;
import com.urcompliant.scheduler.dao.ScheduleEnum;
import com.urcompliant.service.PasswordPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduleFormValidator {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("EEEE dd'XX' MMMM YYYY 'at' HH.mm 'hours'");

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleFormValidator.class);

    private static final int MINUTES_IN_HOUR=60;

    private final PasswordPolicyService policyService;

    private final int minPasswordLength;

    private DS_UserService dsUserService;

    @Autowired
    public ScheduleFormValidator(PasswordPolicyService policyService, @Value("${MPC_SCHEDULER_MIN_PWD_LENGTH:9}") int minPasswordLength, DS_UserService userService) {
        this.policyService = policyService;
        if (minPasswordLength < PasswordPolicyService.LENGTH){
            LOGGER.warn("MPC_SCHEDULER_MIN_PWD_LENGTH: "+minPasswordLength+" < less than Password policy length: "+PasswordPolicyService.LENGTH);
            this.minPasswordLength = PasswordPolicyService.LENGTH;
        } else {
            this.minPasswordLength = minPasswordLength;
        }
        this.dsUserService = userService;
    }

    public void addNext(Model model, LocalDateTime time){
        if (time==null) return;
        String date = time.format(FORMAT);
        int day = time.getDayOfMonth();
        String dayEnding;
        switch(day) {
            case 1:
                dayEnding = "st";
                break;
            case 2:
                dayEnding = "nd";
                break;
            case 3:
                dayEnding = "rd";
                break;
            default:
                dayEnding = "th";
        }
        model.addAttribute("next", date.replace("XX",dayEnding));
    }

    public List<String> validateSchedule(ReportCriteriaForm reportCriteriaForm, BindingResult bindingResult, Model model){
        List<String> errors = new LinkedList<>();
        if (reportCriteriaForm.getZipPass()!=null && (!policyService.passwordValidByPolicy(reportCriteriaForm.getZipPass())  || reportCriteriaForm.getZipPass().length()< minPasswordLength)){
            bindingResult.rejectValue("zipPass","This must be at least "+ minPasswordLength +" characters long and contain at least one uppercase letter, one lowercase letter and one special character (!@#$%^&*-_=+;:,.).","This must be at least "+ minPasswordLength +" characters long and contain at least one uppercase letter, one lowercase letter and one special character (!@#$%^&*-_=+;:,.).");
            errors.add("Zip Password Error");
        }
        if (reportCriteriaForm.getReportName()==null || reportCriteriaForm.getReportName().equals("")){
            bindingResult.rejectValue("reportName","Must set a Report Name","Must set a Report Name");
            errors.add("missing Report Name ");
        }
        if (!reportCriteriaForm.isScheduleEnabled()){
            bindingResult.rejectValue("reportName","Scheduling Not Enabled","Scheduling Not Enabled");
            errors.add("Scheduling Not Enabled");
        }
        model.addAttribute("reportName",reportCriteriaForm.getReportName());
        int schedule = reportCriteriaForm.getScheduleRepeatOption();
        model.addAttribute("frequency", ScheduleEnum.displayText(schedule)+" - "+ScheduleEnum.showingData(schedule));
        model.addAttribute("showingData", ScheduleEnum.showingData(schedule));
        String recip = reportCriteriaForm.getRecipients();
        if (recip!=null && !recip.equals("")){
            Set<String> emails = dsUserService.getByGroupForPrinciple().stream().map(x->x.getUsername().toLowerCase()).collect(Collectors.toSet());
            String recipients[] = recip.split(",");
            for(String recipient : recipients){
                String reci1 = recipient.trim();
                if (!emails.contains(reci1.toLowerCase())){
                    bindingResult.rejectValue("recipients", recipient+" not user in group", recipient+" not a user in group");
                }
            }
        }
        if (schedule== ScheduleEnum.NOW.getId()) {
            if (reportCriteriaForm.isStartDateEnabled()) {
                if (reportCriteriaForm.getStartDateLdt() == null) {
                    bindingResult.rejectValue("startDate", "Enter Start Date", "Enter Start Date");
                    errors.add("Enter Start Date");
                }
            }
            if (reportCriteriaForm.isEndDateEnabled()) {
                if (reportCriteriaForm.getEndDateLdt() == null) {
                    bindingResult.rejectValue("endDate", "Enter End Date", "Enter End Date");
                    errors.add("Enter End Date");
                }
            }
            if (reportCriteriaForm.isAsOfDateEnabled()){
                if (reportCriteriaForm.getAsOfDateLdt() == null) {
                    bindingResult.rejectValue("asOfDate", "Enter As Of Date", "Enter As Of Date");
                    errors.add("Enter As Of Date");
                }
            }
        }
        if (schedule== ScheduleEnum.WEEKLY.getId()) {
            if (reportCriteriaForm.getDayOfWeekOption() == 0) {
                bindingResult.rejectValue("dayOfWeekOption", "Enter Day of Week","Enter Day of Week");
                errors.add("Enter Day of week");
            }
        }
        if (schedule== ScheduleEnum.MONTHLY.getId() || schedule==ScheduleEnum.YEARLY.getId()) {
            if (reportCriteriaForm.getDayOfMonthOption() == 0) {
                bindingResult.rejectValue("dayOfMonthOption", "Enter Day of Month","Enter Day of Month");
                errors.add("Enter Day of Month");
            }
        }
        if ( schedule==ScheduleEnum.YEARLY.getId()) {
            if (reportCriteriaForm.getDayOfMonthOption() == 0) {
                bindingResult.rejectValue("monthOfYearOption", "Enter Month Of Year","Enter Month Of Year");
                errors.add("Enter Month of Year");
            }
        }
        if (schedule!= ScheduleEnum.NOW.getId()) {
            if (reportCriteriaForm.getHourOfDayOption() < ReportCriteriaFormSupportHourOfDay.SCHEDULER_START_HOUR || reportCriteriaForm.getHourOfDayOption()> ReportCriteriaFormSupportHourOfDay.SCHEDULER_END_HOUR) {
                bindingResult.rejectValue("hourOfDayOption", "Scheduling not allowed before 9am or after 9pm","Scheduling not allowed before 9am or after 9pm");
                errors.add("Illegal Hour");
            }
            if (reportCriteriaForm.getMinuteOfHourOption()<0 || reportCriteriaForm.getMinuteOfHourOption()>= MINUTES_IN_HOUR){
                bindingResult.rejectValue("minuteOfHourOption","Select minute of hour", "Select minute of hour");
            }
        }
        return errors;
    }

}
