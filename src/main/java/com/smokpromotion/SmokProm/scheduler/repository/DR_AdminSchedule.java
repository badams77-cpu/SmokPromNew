package com.smokpromotion.SmokProm.scheduler.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urcompliant.config.admin.AdminSecurityPrinciple;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.form.ReportCriteriaForm;
import com.urcompliant.scheduler.dao.DE_Schedule;
import com.urcompliant.scheduler.dao.DE_ScheduleLogEntry;
import com.urcompliant.scheduler.dao.DE_ScheduleWithLastLog;
import com.urcompliant.scheduler.dao.ScheduleEnum;
import com.urcompliant.scheduler.service.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Profile("admin")
public class DR_AdminSchedule
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DR_AdminSchedule.class);

    private static final String URC_EMAIL = "@urcompliant.com";

    private final MPCAppDBConnectionFactory dbFactory;
    private ObjectMapper mapper;
    private static final String TABLE_NAME  = "admin_schedule_rpts";
    private final String overrideEmail;
    private DR_ScheduleLogEntry drScheduleLogEntry;
    private CryptoService cryptoService;


    @Autowired
    public DR_AdminSchedule(
            MPCAppDBConnectionFactory dbFactory,
            ObjectMapper mapper,
            CryptoService cryptoService,
            @Value("${MPC_OVERRIDE_SCHEDULE_EMAIL:null}") String overrideEmail,
            DR_ScheduleLogEntry drScheduleLogEntry
    ) {
        this.overrideEmail = overrideEmail;
        this.dbFactory = dbFactory;
        this.mapper = mapper;
        this.drScheduleLogEntry = drScheduleLogEntry;
        this.cryptoService = cryptoService;
    }

    public List<DE_Schedule> allActiveNotRunInLastHour(PortalEnum portal) {
        Timestamp lastrun = Timestamp.valueOf(LocalDateTime.now().minus(1L, ChronoUnit.HOURS));
        try {
            return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                    "SELECT shed.* \n" +
                            "FROM " + TABLE_NAME + " shed \n" +
                            "WHERE shed.active AND NOT shed.deleted AND (lastrun<? OR lastrun IS NULL)\n" +
                            "ORDER BY id DESC\n",
                    new Object[]{lastrun},
                    new DeScheduleMapper())).orElse(new LinkedList<>());
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return new LinkedList<>();
        }
    }

    public List<DE_Schedule> getAll(PortalEnum portal) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                    "SELECT shed.* \n" +
                            "FROM " + TABLE_NAME + " shed \n" +
                            "ORDER BY id DESC\n",
                    new DeScheduleMapper())).orElse(new LinkedList<>());
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return new LinkedList<>();
        }
    }

    public List<DE_ScheduleWithLastLog> allForUser(PortalEnum portal, String username) {
        try {
        return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                "SELECT sched.id, sched.active, sched.portal,sched.username,sched.dest_email,sched.firstname,sched.lastname,sched.reportname, sched.zip_pass, sched.schedule, \n" +
                        " sched.day_of_week, sched.day_of_month, sched.month_of_year,sched.minutes_of_hour, sched.hour_of_day, sched.lastrun, sched.class, sched.method, sched.report_criteria_form, sched.start_date_days_ago,\n "+
                        " sched.created_datetime, sched.deleted, " +
                        "sl.id as log_id, sl.schedule_id, sl.start_time, sl.runtime, sl.status as status ,sl.attachment_size as attachment_size, sl.exception_class, sl.exception_message, sl.exception_stacktrace, sl.cause_class, sl.cause_message, sl.cause_stacktrace\n"+
                        "FROM "+TABLE_NAME+" sched \n"+
                         " LEFT JOIN "+DR_AdminScheduleLogEntry.TABLE_NAME+" sl ON sl.schedule_id=sched.id AND sched.last_started=sl.start_time AND sched.deleted=0\n"+
                        "WHERE sched.username = ? AND NOT sched.deleted \n"+
                        "ORDER BY sched.id DESC\n",
                new Object[]{ username},
                new DeScheduleMapperWithLastLog())).orElse(new LinkedList<>());
        }catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return new LinkedList<>();
        }
    }

    public List<DE_ScheduleWithLastLog> allForAdmin(PortalEnum portal) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                    "SELECT sched.id, sched.practiceGroupId, sched.active, sched.portal, sched.username,sched.dest_email,sched.firstname,sched.lastname,sched.reportname, sched.zip_pass, sched.schedule, \n" +
                            " sched.day_of_week, sched.day_of_month, sched.month_of_year,sched.minutes_of_hour, sched.hour_of_day, sched.lastrun, sched.class, sched.method, sched.report_criteria_form, sched.start_date_days_ago, sched.created_datetime, " +
                            " sched.deleted,\n " +
                            " sl.id as log_id, sl.schedule_id, sl.start_time, sl.runtime, sl.status as status,sl.attachment_size as attachment_size, sl.exception_class, sl.exception_message, sl.exception_stacktrace, sl.cause_class, sl.cause_message, sl.cause_stacktrace\n" +
                            "FROM "+TABLE_NAME+" sched \n" +
                            " LEFT JOIN "+DR_AdminScheduleLogEntry.TABLE_NAME+" sl ON sl.schedule_id=sched.id AND sched.last_started=sl.start_time \n" +
                            "ORDER BY sched.id DESC\n",
                    new Object[]{},
                    new DeScheduleMapperWithLastLog())).orElse(new LinkedList<>());
        } catch (DataAccessException e){
                LOGGER.warn("Data Access Exception reading table",e);
                return new LinkedList<>();
        }
    }

    public List<DE_ScheduleWithLastLog> getByDateTimesAndGroupNameForAdmin(PortalEnum portal, String groupName, LocalDateTime start, LocalDateTime end) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                    "SELECT sched.id, sched.practiceGroupId, sched.active, sched.portal, sched.username,sched.dest_email,sched.firstname,sched.lastname,sched.reportname, sched.zip_pass, sched.schedule, \n" +
                            " sched.day_of_week, sched.day_of_month, sched.month_of_year,sched.minutes_of_hour, sched.hour_of_day, sched.lastrun, sched.class, sched.method, sched.report_criteria_form, sched.start_date_days_ago, sched.created_datetime, " +
                            " sched.deleted,\n " +
                            " sl.id as log_id, sl.schedule_id, sl.start_time, sl.runtime, sl.status as status,sl.attachment_size as attachment_size, sl.exception_class, sl.exception_message, sl.exception_stacktrace, sl.cause_class, sl.cause_message, sl.cause_stacktrace\n" +
                            "FROM "+TABLE_NAME+" sched \n" +
                            " LEFT JOIN tblPracticeGroup pg ON sched.practiceGroupId=pg.PracticeGroupID\n"+
                            " LEFT JOIN "+DR_AdminScheduleLogEntry.TABLE_NAME+" sl ON sl.schedule_id=sched.id AND sched.last_started=sl.start_time \n" +
                            " WHERE pg.PracticeGroupName like ? AND ((sched.lastrun BETWEEN ? AND ?) OR (sched.created_datetime BETWEEN ? AND ?))\n"+
                            "ORDER BY sched.id DESC\n",
                    new Object[]{ "%"+groupName+"%", Timestamp.valueOf(start), Timestamp.valueOf(end), Timestamp.valueOf(start), Timestamp.valueOf(end)},
                    new DeScheduleMapperWithLastLog())).orElse(new LinkedList<>());
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return new LinkedList<>();
        }
    }


    /**
     * Use for UI work, do limit schedule viewed to correct user
     * @param portal
     * @param id
     * @param username
     * @return Optional<DE_Schedule> The named schedume
     */
    public Optional<DE_Schedule> getById(PortalEnum portal, int id, String username) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                    "SELECT shed.* \n" +
                            "FROM " + TABLE_NAME + " shed \n" +
                            "WHERE username = ? AND id=? \n" +
                            "ORDER BY id DESC\n",
                    new Object[]{username, id},
                    new DeScheduleMapper())).orElse(new LinkedList<>()).stream().findFirst();
        } catch (DataAccessException e) {
            LOGGER.warn("Data Access Exception reading table", e);
            return Optional.empty();
        }
    }

    /**
     * Use for UI work, do limit schedule viewed to correct user
     * @param portal
     * @param id
     * @return Optional<DE_Schedule> The named schedume
     */
    public Optional<DE_Schedule> getByIdAdmin(PortalEnum portal, int id) {
        return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                "SELECT shed.* \n" +
                        "FROM "+TABLE_NAME+" shed \n"+
                        "WHERE id=? \n"+
                        "ORDER BY id DESC\n",
                new Object[]{ id},
                new DeScheduleMapper())).orElse(new LinkedList<>()).stream().findFirst();
    }

    /**
     * Use only internal
     *
     * @param portal
     * @param id
     * @return Optional<DE_Schedule> The Schedule with the ID
     */
    public Optional<DE_Schedule> getById(PortalEnum portal, int id) {
        try {
        return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                "SELECT shed.* \n" +
                        "FROM "+TABLE_NAME+" shed \n"+
                        "WHERE id=? \n"+
                        "ORDER BY id DESC\n",
                new Object[]{  id},
                new DeScheduleMapper())).orElse(new LinkedList<>()).stream().findFirst();
        } catch (DataAccessException e) {
            LOGGER.warn("Data Access Exception reading table", e);
            return Optional.empty();
        }

    }

    public boolean setLastRun(PortalEnum legacyPortal, int id, LocalDateTime lastSent){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                    "UPDATE " + TABLE_NAME + " SET lastrun = ? WHERE id = ?", Timestamp.valueOf(lastSent), id)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e){
                LOGGER.warn("Data Access Exception reading table",e);
                return false;
        }
    }

    public boolean setLastStarted(PortalEnum legacyPortal, int id, LocalDateTime lastSent){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET last_started = ? WHERE id = ?", Timestamp.valueOf(lastSent), id)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return false;
        }
    }

    public boolean deleteByUsername(PortalEnum legacyPortal, String username){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                    "UPDATE "+TABLE_NAME+" SET deleted=1 WHERE username = ?",  username)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return false;
        }
    }

    public boolean delete(PortalEnum legacyPortal, int id, String username){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET deleted=1 WHERE id = ? AND username = ?", id, username)).orElse(0);
        return rowsAffected == 1;
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return false;
        }
    }

    public boolean deleteAdmin(PortalEnum legacyPortal, int id){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET deleted=1 WHERE id = ?", id)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            LOGGER.warn("Data Access Exception reading table", e);
           return false;
        }
    }

    public boolean undeleteAdmin(PortalEnum legacyPortal, int id){
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET deleted=0 WHERE id = ?", id)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            LOGGER.warn("Data Access Exception reading table", e);
            return false;
        }
    }

    public boolean setActive(PortalEnum legacyPortal, int id, String username, boolean active) {
        try {
            int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                    "UPDATE " + TABLE_NAME + " SET active=? WHERE id = ? AND username = ?", active, id, username)).orElse(0);
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            LOGGER.warn("Data Access Exception reading table", e);
            return false;
        }
    }

    public boolean setActiveAdmin(PortalEnum legacyPortal, int id, boolean active){
        int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET active=? WHERE id = ?",active, id)).orElse(0);
        return rowsAffected == 1;
    }

    public boolean updateForm(PortalEnum legacyPortal, int id, String form){
        int rowsAffected = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.update(
                "UPDATE "+TABLE_NAME+" SET report_criteria_form=? WHERE id = ?", form,id)).orElse(0);
        return rowsAffected == 1;
    }

    public LocalDateTime scheduleReport(PortalEnum portal, AdminSecurityPrinciple principle, ReportCriteriaForm form, Class controller, String method) {

        int id = 0;
        if (portal == null) {
            LOGGER.error("create - null portal [" + portal + "]");
        } else {
            try {
                String sql = "INSERT INTO " + TABLE_NAME +
                        " (active,username, portal, reportname, firstname, lastname , dest_email, schedule, day_of_week, day_of_month,month_of_year, hour_of_day, minutes_of_hour,class,method,report_criteria_form, zip_pass, created_datetime, deleted) values "+
                        "(:active, :username, :portal, :reportname, :firstname, :lastname, :dest_email, :schedule, :day_of_week, :day_of_month, :month_of_year, :hour_of_day, :minute_of_hour, :class, :method, :report_criteria_form, :zip_pass, now(), 0);";
                KeyHolder holder = new GeneratedKeyHolder();
                SqlParameterSource parameterSource = (new ScheduleParameterSourceCreator(principle, form, controller, method, LocalDateTime.now(), mapper)).getParameterSource();
                dbFactory.getNamedParameterJdbcTemplate(portal).ifPresent(template->template.update(sql, parameterSource, holder));
                Number key = holder.getKey();
                if (key==null){
                    LOGGER.error("Could not create schedule row");
                    return null;
                }
                id = holder.getKey().intValue();
                LOGGER.info("Create Schedule " + id);
            } catch (Exception e) {
                LOGGER.error("Could not add schedule",e);
                return null;
            }
        }
        return getNextRun(form);
    }

    protected class ScheduleParameterSourceCreator {
        private final AdminSecurityPrinciple principle;
        private final ReportCriteriaForm form;
        private final Class theClass;
        private final String method;
        private final LocalDateTime created;
        private final ObjectMapper mapper;

        public ScheduleParameterSourceCreator(AdminSecurityPrinciple principle, ReportCriteriaForm form, Class theClass, String method, LocalDateTime created, ObjectMapper mapper){
            this.principle = principle;
            this.form = form;
            this.theClass = theClass;
            this.method = method;
            this.created = created;
            this.mapper = mapper;
        }

        public SqlParameterSource getParameterSource() throws SQLException{
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("active", true);
            paramMap.put("username", principle.getEmail());
            paramMap.put("practiceGroupId", 0);
            paramMap.put("portal", PortalEnum.AWS);
            paramMap.put("reportname", form.getReportName());
            paramMap.put("firstname", principle.getFirstname());
            paramMap.put("lastname", principle.getLastname());
            if (overrideEmail!=null && overrideEmail.endsWith(URC_EMAIL)) {
                paramMap.put("dest_email", overrideEmail);
            } else {
                paramMap.put("dest_email", principle.getEmail());
            }
            paramMap.put("schedule",form.getScheduleRepeatOption()+1);
            paramMap.put("day_of_week",form.getDayOfWeekOption());
            paramMap.put("day_of_month",form.getDayOfMonthOption());
            paramMap.put("month_of_year",form.getMonthOfYearOption());
            paramMap.put("hour_of_day",form.getHourOfDayOption());
            paramMap.put("minute_of_hour",form.getMinuteOfHourOption());
            paramMap.put("class",theClass.getSimpleName());
            paramMap.put("method",method);
            paramMap.put("zip_pass", cryptoService.encrypt(form.getZipPass()));
            form.setZipPass("");

            String formString = "";
            try {
                formString = mapper.writeValueAsString(form);
            } catch (Exception  e){
                LOGGER.warn("getSqlParameterSource: Error mapper reportCriteriaForm ",e);
            }
            paramMap.put("report_criteria_form", formString);
            paramMap.put("created_datetime", Timestamp.valueOf(created));
            for(String key :paramMap.keySet()){
                if (paramMap.get(key)==null){
                    throw new SQLException("param "+key+" was null");
                }
            }
            return new MapSqlParameterSource(paramMap);
        }

    }

    protected ScheduleParameterSourceCreator getScheduleStatementCreator( AdminSecurityPrinciple principle, ReportCriteriaForm form, Class theClass, String method,  LocalDateTime created){
        return new ScheduleParameterSourceCreator( principle,form,theClass, method, created, mapper);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------

    private LocalDateTime getNextRun(ReportCriteriaForm form){
        switch (ScheduleEnum.fromId(form.getScheduleRepeatOption())){
            case NOW :
                return LocalDateTime.now();
            case DAILY :
                 LocalDateTime then =  LocalDateTime.now()
                        .with(ChronoField.HOUR_OF_DAY, form.getHourOfDayOption())
                        .with(ChronoField.MINUTE_OF_HOUR,form.getMinuteOfHourOption())
                        .with(ChronoField.SECOND_OF_MINUTE,0)
                        .with(ChronoField.NANO_OF_SECOND,0);
                 if (then.isBefore(LocalDateTime.now() )){
                     return then.plusDays(1);
                 } else {
                     return then;
                 }
            case WEEKLY :
                LocalDateTime then1 = LocalDateTime.now()
                        .with(ChronoField.DAY_OF_WEEK, form.getDayOfWeekOption())
                        .with(ChronoField.HOUR_OF_DAY, form.getHourOfDayOption())
                        .with(ChronoField.MINUTE_OF_HOUR,form.getMinuteOfHourOption())
                        .with(ChronoField.SECOND_OF_MINUTE,0)
                        .with(ChronoField.NANO_OF_SECOND,0);
                if (then1.isBefore(LocalDateTime.now())){
                    return then1.plusDays(7);
                } else {
                    return then1;
                }
            case MONTHLY:
                LocalDateTime then2 = LocalDateTime.now()
                        .with(ChronoField.DAY_OF_MONTH, form.getDayOfMonthOption())
                        .with(ChronoField.HOUR_OF_DAY, form.getHourOfDayOption())
                        .with(ChronoField.MINUTE_OF_HOUR,form.getMinuteOfHourOption())
                        .with(ChronoField.SECOND_OF_MINUTE,0)
                        .with(ChronoField.NANO_OF_SECOND,0);
                if (then2.isBefore(LocalDateTime.now())){
                    return then2.plus(1,ChronoUnit.MONTHS);
                } else {
                    return then2;
                }
            case YEARLY:
                LocalDateTime then3 = LocalDateTime.now()
                     .with(ChronoField.MONTH_OF_YEAR, form.getMonthOfYearOption())
                     .with(ChronoField.DAY_OF_MONTH, form.getDayOfMonthOption())
                     .with(ChronoField.HOUR_OF_DAY, form.getHourOfDayOption())
                     .with(ChronoField.MINUTE_OF_HOUR,form.getMinuteOfHourOption())
                     .with(ChronoField.SECOND_OF_MINUTE,0)
                     .with(ChronoField.NANO_OF_SECOND,0);
                if (then3.isBefore(LocalDateTime.now())){
                    return then3.plus(1,ChronoUnit.YEARS);
                } else {
                    return then3;
                }
            default:
                return null;
        }
    }


    protected DeScheduleMapper getScheduleMapper(){
        return new DeScheduleMapper();
    }

    protected class DeScheduleMapper implements RowMapper<DE_Schedule> {
        @Override
        public DE_Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {

            DE_Schedule schedule = new DE_Schedule();
            schedule.setId(rs.getInt("id"));
            schedule.setActive(rs.getBoolean("active"));
            schedule.setUsername(rs.getString("username"));
            schedule.setPortal(rs.getString("portal"));
            schedule.setReportname(rs.getString("reportname"));
            schedule.setDestEmail(rs.getString("dest_email"));
            schedule.setSchedule(ScheduleEnum.valueOf(rs.getString("schedule")));
            schedule.setDayOfWeek(rs.getInt("day_of_week"));
            schedule.setDayOfMonth(rs.getInt("day_of_month"));
            schedule.setMonthOfYear(rs.getInt("month_of_year"));
            schedule.setHourOfDay(rs.getInt("hour_of_day"));
            schedule.setMinutesOfHour(rs.getInt("minutes_of_hour"));
            Timestamp lastrun = rs.getTimestamp("lastrun");
            if (lastrun!=null) {
                schedule.setLastrun(lastrun.toLocalDateTime());
            }
            schedule.setTheclass(rs.getString("class"));
            schedule.setMethod(rs.getString("method"));
            schedule.setReportCriteriaForm(rs.getString("report_criteria_form"));
            schedule.setStartDateDaysAgo(rs.getInt("start_date_days_ago"));
            schedule.setZipPass(rs.getString("zip_pass"));
            schedule.setLastname(rs.getString("lastname"));
            schedule.setFirstname(rs.getString("firstname"));
            schedule.setReportname(rs.getString("reportname"));
            schedule.setDeleted(rs.getBoolean("deleted"));
            Timestamp created = rs.getTimestamp("created_datetime");
            if (created!=null) {
                schedule.setCreatedDateTime(created.toLocalDateTime());
            } else {
                schedule.setCreatedDateTime(LocalDateTime.MIN);
            }
            return schedule;

        }
    }


    protected class DeScheduleMapperWithLastLog implements RowMapper<DE_ScheduleWithLastLog> {
        @Override
        public DE_ScheduleWithLastLog mapRow(ResultSet rs, int rowNum) throws SQLException {

            DE_ScheduleWithLastLog schedule = new DE_ScheduleWithLastLog();
            schedule.setId(rs.getInt("id"));
            schedule.setActive(rs.getBoolean("active"));
            schedule.setUsername(rs.getString("username"));
            schedule.setPortal(rs.getString("portal"));
            schedule.setReportname(rs.getString("reportname"));
            schedule.setDestEmail(rs.getString("dest_email"));
            schedule.setSchedule(ScheduleEnum.valueOf(rs.getString("schedule")));
            schedule.setDayOfWeek(rs.getInt("day_of_week"));
            schedule.setDayOfMonth(rs.getInt("day_of_month"));
            schedule.setMonthOfYear(rs.getInt("month_of_year"));
            schedule.setHourOfDay(rs.getInt("hour_of_day"));
            schedule.setMinutesOfHour(rs.getInt("minutes_of_hour"));
            Timestamp lastrun = rs.getTimestamp("lastrun");
            if (lastrun!=null) {
                schedule.setLastrun(lastrun.toLocalDateTime());
            }
            schedule.setTheclass(rs.getString("class"));
            schedule.setMethod(rs.getString("method"));
            schedule.setReportCriteriaForm(rs.getString("report_criteria_form"));
            schedule.setStartDateDaysAgo(rs.getInt("start_date_days_ago"));
            schedule.setZipPass(rs.getString("zip_pass"));
            schedule.setLastname(rs.getString("lastname"));
            schedule.setFirstname(rs.getString("firstname"));
            schedule.setReportname(rs.getString("reportname"));
            schedule.setDeleted(rs.getBoolean("deleted"));
            Timestamp created = rs.getTimestamp("created_datetime");
            if (created!=null) {
                schedule.setCreatedDateTime(created.toLocalDateTime());
            } else {
                schedule.setCreatedDateTime(LocalDateTime.MIN);
            }
            DR_ScheduleLogEntry.DeScheduleLogMapper logMapper = drScheduleLogEntry.getScheduleMapper();
            DE_ScheduleLogEntry log = logMapper.mapRow(rs, rowNum);
            schedule.setLastLog(log);
            log.setId(rs.getInt("log_id"));
            return schedule;

        }
    }
}
