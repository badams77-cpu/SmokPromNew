package com.smokpromotion.SmokProm.scheduler.repository;

import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import com.urcompliant.scheduler.dao.DE_ScheduleLogEntry;
import com.urcompliant.scheduler.dao.DE_SchedulerMetrics;
import com.urcompliant.scheduler.dao.StatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DR_SchedulerMetrics
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DR_SchedulerMetrics.class);

    private final MPCAppDBConnectionFactory dbFactory;
    private static final String TABLE_NAME  = "scheduled_rpts_metrics";

    @Autowired
    public DR_SchedulerMetrics(
            MPCAppDBConnectionFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    public List<DE_SchedulerMetrics> allInDateRange (PortalEnum portal, LocalDate start, LocalDate end) {
        return dbFactory.getJdbcTemplate(portal).map(template->template.query(
                "SELECT metrics.* \n" +
                        "FROM "+TABLE_NAME+" metrics \n"+
                        "WHERE  (log_date BETWEEN  ? AND ?)\n"+
                        "ORDER BY id DESC\n",
                new Object[]{ Date.valueOf(start), Date.valueOf(end)},
                new DeScheduleMetricsMapper())).orElse(new LinkedList<>());
    }




    public void saveEntry(PortalEnum portal, DE_SchedulerMetrics entry) {

        int id = 0;
        if (portal == null) {
            LOGGER.error("saveEntry - null portal [" + portal + "]");
        } else {
            try {
                List<DE_SchedulerMetrics> existingEntries = allInDateRange(portal, entry.getDate(),entry.getDate());
                if (existingEntries.isEmpty()) {


                    String sql = "INSERT INTO " + TABLE_NAME +
                            " (log_date, failure_count, success_count, run_count, seconds_not_running, seconds_running, seconds_queued, seconds_queue_empty)"
                            + " values " +
                            " (:log_date, :failure_count, :success_count, :run_count, :seconds_not_running, :seconds_running, :seconds_queued, :seconds_queue_empty);";
                    KeyHolder holder = new GeneratedKeyHolder();
                    SqlParameterSource parameterSource = (new ScheduleMetricsParameterSource(entry)).getParameterSource();
                    dbFactory.getNamedParameterJdbcTemplate(portal).ifPresent(template->template.update(sql, parameterSource, holder));
                    if (holder.getKey()==null){
                        LOGGER.info("Could not insert Schedule Metrics");
                    }
                    id = holder.getKey().intValue();
                } else {
                    String sql = "UPDATE " + TABLE_NAME +" SET " +
                            " failure_count = :failure_count, success_count=:success_count, run_count=:run_count,"+
                            " seconds_not_running = :seconds_not_running, seconds_running = :seconds_running, seconds_queued = :seconds_queued, seconds_queue_empty = :seconds_queue_empty "
                            +" where log_date = :log_date";
                    SqlParameterSource parameterSource = (new ScheduleMetricsParameterSource(entry)).getParameterSource();
                    dbFactory.getNamedParameterJdbcTemplate(portal).ifPresent(feature->feature.update(sql, parameterSource));
                }
            } catch (Exception e) {
                LOGGER.error("Could not add schedule metrics entry for report "+entry.getDate().format(DateTimeFormatter.ISO_DATE),e);
            }
        }
    }

    protected class ScheduleMetricsParameterSource {

        private final DE_SchedulerMetrics entry;

        public ScheduleMetricsParameterSource( DE_SchedulerMetrics entry){
            this.entry = entry;
        }

        public SqlParameterSource getParameterSource() throws SQLException{
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("log_date", Date.valueOf(entry.getDate()));
            paramMap.put("failure_count", entry.getFailureCount());
            paramMap.put("success_count", entry.getSuccessCount());
            paramMap.put("run_count", entry.getRunCount());
            paramMap.put("seconds_not_running", entry.getSecondsNotRunning());
            paramMap.put("seconds_running", entry.getSecondsRunning());
            paramMap.put("seconds_queued", entry.getSecondsQueued());
            paramMap.put("seconds_queue_empty", entry.getSecondsQueueEmpty());
            return new MapSqlParameterSource(paramMap);
        }

    }

    public  ScheduleMetricsParameterSource getScheduleMetricsParameterSource( DE_SchedulerMetrics entry){
        return new ScheduleMetricsParameterSource(entry);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------


    public DeScheduleMetricsMapper getScheduleMapper(){
        return new DeScheduleMetricsMapper();
    }

    protected class DeScheduleMetricsMapper implements RowMapper<DE_SchedulerMetrics> {
        @Override
        public DE_SchedulerMetrics mapRow(ResultSet rs, int rowNum) throws SQLException {

            DE_SchedulerMetrics metrics = new DE_SchedulerMetrics();
            Date date = rs.getDate("log_date");
            metrics.setDate(date==null? null : date.toLocalDate());
            metrics.setSuccessCount(rs.getInt("success_count"));
            metrics.setFailureCount(rs.getInt("failure_count"));
            metrics.setRunCount(rs.getInt("run_count"));
            metrics.setSecondsRunning(rs.getInt("seconds_running"));
            metrics.setSecondsNotRunning(rs.getInt("seconds_not_running"));
            metrics.setSecondsQueued(rs.getInt("seconds_queued"));
            metrics.setSecondsQueueEmpty(rs.getInt("seconds_queue_empty"));
            return metrics;

        }
    }


}
