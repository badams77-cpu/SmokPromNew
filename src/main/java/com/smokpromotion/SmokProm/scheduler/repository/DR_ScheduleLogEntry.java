package com.smokpromotion.SmokProm.scheduler.repository;

import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.scheduler.dao.DE_ScheduleLogEntry;
import com.urcompliant.scheduler.dao.StatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DR_ScheduleLogEntry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DR_ScheduleLogEntry.class);
    public static final int MESSAGE_MAX_LENGTH = 255;

    private final MPCAppDBConnectionFactory dbFactory;

    protected static final String TABLE_NAME  = "schedule_rpts_log";

    @Autowired
    public DR_ScheduleLogEntry(
            MPCAppDBConnectionFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    public List<DE_ScheduleLogEntry> allInDateRange (PortalEnum portal, LocalDateTime start, LocalDateTime end) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(feature->feature.query(
                    "SELECT shed.* \n" +
                            "FROM " + TABLE_NAME + " shed \n" +
                            "WHERE  (starttime BETWEEN  ? AND ?)\n" +
                            "ORDER BY id DESC\n",
                    new Object[]{Timestamp.valueOf(start), Timestamp.valueOf(end)},
                    new DeScheduleLogMapper())).orElse(new LinkedList<>());
        } catch (DataAccessException e){
            LOGGER.warn("Data Access Exception reading table",e);
            return new LinkedList<>();
        }
    }

    public List<DE_ScheduleLogEntry> allForSchedule(PortalEnum portal, int scheduleId) {
        try {
            return dbFactory.getJdbcTemplate(portal).map(feature->feature.query(
                    "SELECT shed.* \n" +
                            "FROM " + TABLE_NAME + " shed \n" +
                            "WHERE (schedule_id=?)\n" +
                            "ORDER BY id DESC\n",
                    new Object[]{new Integer(scheduleId)},
                    new DeScheduleLogMapper())).orElse(new LinkedList<>());
        }  catch (DataAccessException e){
                LOGGER.warn("Data Access Exception reading table",e);
                return new LinkedList<>();
            }
    }


    public void saveEntry(PortalEnum portal, DE_ScheduleLogEntry entry) {

        int id = 0;
        if (portal == null) {
            LOGGER.error("create - null portal [" + portal + "]");
        } else {
            try {
                String sql = "INSERT INTO " + TABLE_NAME +
                        " (schedule_id,start_time,runtime, status,attachment_size,exception_class,exception_message,exception_stacktrace, cause_class, cause_message, cause_stacktrace)"
                +       " values "+
                        " ( :schedule_id, :start_time, :runtime, :status, :attachment_size, :exception_class, :exception_message, :exception_stacktrace, :cause_class, :cause_message, :cause_stacktrace);";
                KeyHolder holder = new GeneratedKeyHolder();
                SqlParameterSource parameterSource = (new ScheduleLogParameterSourceCreator(entry)).getParameterSource();
                dbFactory.getNamedParameterJdbcTemplate(portal).ifPresent(x->x.update(sql, parameterSource, holder));
                if (holder.getKey()==null){
                    LOGGER.error("Could not create ScheduleLogEntry");
                    return;
                }
                id = holder.getKey().intValue();
                LOGGER.info("Log Scheduled for report "+entry.getScheduleId()+" schedule_log_id " + id);
            } catch (Exception e) {
                LOGGER.error("Could not add schedule log entry for report "+entry.getScheduleId(),e);
            }
        }
    }

    protected class ScheduleLogParameterSourceCreator {

        private final DE_ScheduleLogEntry entry;

        public ScheduleLogParameterSourceCreator( DE_ScheduleLogEntry entry){
            this.entry = entry;
        }

        public SqlParameterSource getParameterSource() throws SQLException {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("schedule_id", entry.getScheduleId());
            paramMap.put("start_time", Timestamp.valueOf(entry.getStartTime()));
            paramMap.put("runtime", entry.getRuntime());
            paramMap.put("status", entry.getStatus().getId());
            paramMap.put("attachment_size", entry.getAttachmentSize());
            paramMap.put("exception_class", entry.getExceptionClass());
            String message = "";
            if (entry.getExceptionMessage()!=null){
                message = entry.getExceptionMessage();
                if (message.length()> MESSAGE_MAX_LENGTH){
                    message = message.substring(0,MESSAGE_MAX_LENGTH);
                }
            }
            paramMap.put("exception_message", message);
            paramMap.put("exception_stacktrace", entry.getExceptionStackTrace());
            paramMap.put("cause_class", entry.getCauseClass());
            paramMap.put("cause_message", entry.getCauseMessageTruncated(255));
            paramMap.put("cause_stacktrace", entry.getCauseStackTrace());
            return new MapSqlParameterSource(paramMap);
        }

    }

    public ScheduleLogParameterSourceCreator getScheduleLogParameterCreator( DE_ScheduleLogEntry entry){
        return new ScheduleLogParameterSourceCreator( entry);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------



    public DeScheduleLogMapper getScheduleMapper(){
        return new DeScheduleLogMapper();
    }

    protected class DeScheduleLogMapper implements RowMapper<DE_ScheduleLogEntry> {
        @Override
        public DE_ScheduleLogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {

            DE_ScheduleLogEntry schedule = new DE_ScheduleLogEntry();
            schedule.setId(rs.getInt("id"));
            schedule.setScheduleId(rs.getInt("schedule_id"));
            Timestamp startTime = rs.getTimestamp("start_time");
            if (startTime!=null) {
                schedule.setStartTime(startTime.toLocalDateTime());
            }
            schedule.setRuntime( rs.getLong("runtime"));
            schedule.setStatus( StatusEnum.fromId(rs.getInt("status")));
            schedule.setAttachmentSize( rs.getLong("attachment_size"));
            schedule.setExceptionClass( rs.getString("exception_class"));
            schedule.setExceptionMessage( rs.getString("exception_message"));
            schedule.setExceptionStackTrace( rs.getString("exception_stacktrace"));
            schedule.setCauseClass( rs.getString("cause_class"));
            schedule.setCauseMessage( rs.getString("cause_message"));
            schedule.setCauseStackTrace( rs.getString("cause_stacktrace"));
            return schedule;

        }
    }


}
