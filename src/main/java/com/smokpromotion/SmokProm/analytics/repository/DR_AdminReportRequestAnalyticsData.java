package com.smokpromotion.SmokProm.analytics.repository;


import com.urcompliant.analytics.AnalyticsSiteEnum;
import com.urcompliant.analytics.entity.AnalyticsUserAllLogins;
import com.urcompliant.analytics.entity.AnalyticsUserLogins;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Profile("admin")
public class DR_AdminReportRequestAnalyticsData  {


    private static final String ADMIN_LOGIN_TABLE = "admin_rpt_logins_group ";

    private static final int SECONDS_IN_LOGIN = 1800;

    private MPCAppDBConnectionFactory dbFactory;

    @Autowired
    public DR_AdminReportRequestAnalyticsData(MPCAppDBConnectionFactory dbFactory){
        this.dbFactory = dbFactory;
    }

    private int clearTempTable(PortalEnum portal) {
        String sqlDelete = "DELETE FROM "+ADMIN_LOGIN_TABLE+";\n";
        int rows= dbFactory.getJdbcTemplate(portal).map(template->template.update(sqlDelete)).orElse(0);
        return rows;
    }

    public List<AnalyticsUserLogins> getLoginsByUserAndDates(PortalEnum portal, AnalyticsSiteEnum site, LocalDate start, LocalDate end, List<String> emails, boolean hideInternal){
        if (emails.isEmpty()){ return new LinkedList<>(); }
        synchronized(this) {
            int rowsDelete = clearTempTable(portal);
            java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0, 0));
            java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23, 59));
            String emailTokens = emails.stream().map(x -> "?").collect(Collectors.joining(","));
            String hideClause = hideInternal(hideInternal);
            Object[] params = new Object[3 + emails.size()];
            params[0] = site.getCode();
            params[1] = startTime;
            params[2] = endTime;
            int insertPos = 3;

            for (String email : emails) {
                params[insertPos++] = email;
            }
            String sqlInsert =
                    " INSERT INTO "+ ADMIN_LOGIN_TABLE+ " (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE " + hideClause + " site=? AND (created_at BETWEEN ? AND ?) AND email IN (" + emailTokens + ") " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, params)).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserLogins> result = getAnalyticsUserLogins(portal);
            return result;
        }
    }

    public List<AnalyticsUserLogins> getLoginsByUserAndTimes(PortalEnum portal, AnalyticsSiteEnum site, LocalDateTime start, LocalDateTime end, List<String> emails, boolean hideInternal){
        if (emails.isEmpty()){ return new LinkedList<>(); }
        synchronized(this) {
            int rowsDelete = clearTempTable(portal);
            String emailTokens = emails.stream().map(x -> "?").collect(Collectors.joining(","));
            String hideClause = hideInternal(hideInternal);
            Object[] params = new Object[3 + emails.size()];
            params[0] = site.getCode();
            params[1] = start;
            params[2] = end;
            int insertPos = 3;
            for (String email : emails) {
                params[insertPos++] = email;
            }
            String sqlInsert =
                    " INSERT INTO "+ ADMIN_LOGIN_TABLE+ " (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE " + hideClause + " site=? AND (created_at BETWEEN ? AND ?) AND email IN (" + emailTokens + ") " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, params)).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserLogins> result = getAnalyticsUserLogins(portal);
            return result;
        }
    }

    public List<AnalyticsUserLogins> getLoginsByDates(PortalEnum portal, AnalyticsSiteEnum site, LocalDate start, LocalDate end){

        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        synchronized (this) {
            int rowsDelete = clearTempTable(portal);
            String sqlInsert =
                    " INSERT INTO "+ADMIN_LOGIN_TABLE+" (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE site=? AND (created_at BETWEEN ? AND ?) " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, new Object[]{site.getCode(), startTime, endTime})).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserLogins> result = getAnalyticsUserLogins(portal);
            return result;
        }
    }

    public List<AnalyticsUserAllLogins> getAllLoginsByDates(PortalEnum portal, AnalyticsSiteEnum site, LocalDate start, LocalDate end){

        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        synchronized (this) {
            int rowsDelete = clearTempTable(portal);
            String sqlInsert =
                    " INSERT INTO "+ADMIN_LOGIN_TABLE+" (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE site=? AND (created_at BETWEEN ? AND ?) " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, new Object[]{site.getCode(), startTime, endTime})).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserAllLogins> result = getAnalyticsUserAllLogins(portal);
            return result;
        }
    }

    public List<AnalyticsUserLogins> getLoginsByTimes(PortalEnum portal, AnalyticsSiteEnum site, LocalDateTime startTime, LocalDateTime endTime){
        synchronized (this) {
            int rowsDelete = clearTempTable(portal);
            String sqlInsert =
                    " INSERT INTO "+ADMIN_LOGIN_TABLE+" (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE site=? AND (created_at BETWEEN ? AND ?) AND uid!=0 " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, new Object[]{site.getCode(), startTime, endTime})).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserLogins> result = getAnalyticsUserLogins(portal);
            return result;
        }
    }

    public List<AnalyticsUserAllLogins> getAllLoginsByTimes(PortalEnum portal, AnalyticsSiteEnum site, LocalDateTime startTime, LocalDateTime endTime){
        synchronized (this) {
            int rowsDelete = clearTempTable(portal);
            String sqlInsert =
                    " INSERT INTO "+ADMIN_LOGIN_TABLE+" (uid,email, created_at) SELECT uid,email, created_at FROM analytics WHERE site=? AND (created_at BETWEEN ? AND ?) AND uid!=0 " +
                            " GROUP BY email, uid, created_at  ORDER BY email ASC, created_at ASC ;\n";
            int rowInsert = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlInsert, new Object[]{site.getCode(), startTime, endTime})).orElse(0);
            int firstRow = firstRow(portal);
            List<AnalyticsUserAllLogins> result = getAnalyticsUserAllLogins(portal);
            return result;
        }
    }

    private List<AnalyticsUserLogins> getAnalyticsUserLogins(PortalEnum portal) {
        String sqlLoginsUpdate = " UPDATE "+ADMIN_LOGIN_TABLE+" lg JOIN "+ADMIN_LOGIN_TABLE+" AS lg0 ON lg0.id=lg.id-1 SET lg.newlogin= CASE WHEN (lg0.email IS NULL OR lg.uid!=lg0.uid " +
                " OR UNIX_TIMESTAMP(lg.created_at)-UNIX_TIMESTAMP(lg0.created_at)>" + SECONDS_IN_LOGIN + ") THEN 1 ELSE 0 END;\n";
        int rowsLogins = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlLoginsUpdate)).orElse(0);
        String sqlRead = " SELECT uid,email, COUNT(newlogin) as logins, MAX(created_at) as lastLoginDate FROM "+ADMIN_LOGIN_TABLE+" WHERE newlogin=1 GROUP BY uid, email;";

        return dbFactory.getJdbcTemplate(portal).map(template -> template.query(sqlRead, new AnalyticsUserLoginsMapper())).orElse(new LinkedList<>());
    }

    private List<AnalyticsUserAllLogins> getAnalyticsUserAllLogins(PortalEnum portal) {
        String sqlLoginsUpdate = " UPDATE "+ADMIN_LOGIN_TABLE+" lg JOIN "+ADMIN_LOGIN_TABLE+" AS lg0 ON lg0.id=lg.id-1 SET lg.newlogin= CASE WHEN (lg0.email IS NULL OR lg.uid!=lg0.uid " +
                " OR UNIX_TIMESTAMP(lg.created_at)-UNIX_TIMESTAMP(lg0.created_at)>" + SECONDS_IN_LOGIN + ") THEN 1 ELSE 0 END;\n";
        int rowsLogins = dbFactory.getJdbcTemplate(portal).map(template -> template.update(sqlLoginsUpdate)).orElse(0);
        String sqlRead = " SELECT uid,email, COUNT(newlogin) as logins, MAX(created_at) as lastLoginDate, DATE(created_at) as loginDate FROM "+ADMIN_LOGIN_TABLE+" WHERE newlogin=1 GROUP BY uid, email, DATE(created_at);";

        List<AnalyticsUserAllLogins> logins= dbFactory.getJdbcTemplate(portal).map(template -> template.query(sqlRead, new AnalyticsUserAllLoginsMapper())).orElse(new LinkedList<>());
        List<LocalDateTime> dates= logins.stream().map(x->x.getLastLoginDate()).sorted().collect(Collectors.toList());
        if (!dates.isEmpty()){
            LocalDateTime maxDate = dates.get(dates.size()-1);
            logins.forEach(x->x.setLastLoginDate(maxDate));
        }
        return logins;
    }

    private int firstRow(PortalEnum portal){
        int rowNumber = dbFactory.getJdbcTemplate(portal).map(template ->
                template.queryForList("SELECT min(id) FROM "+ADMIN_LOGIN_TABLE, Integer.class)).orElse(new LinkedList<Integer>())
                    .stream()
                    .map(Optional::ofNullable)
                    .findFirst()
                    .flatMap(Function.identity()).orElse(0);
        int firstRow = dbFactory.getJdbcTemplate(portal).map(template ->
                template.update(" UPDATE "+ADMIN_LOGIN_TABLE+"SET newlogin=1 where id=?", new Object[]{rowNumber})).orElse(0);
        return rowNumber;
    }

    protected String hideInternal(boolean hide){
        if (hide){ return " NOT (get_action LIKE '%/api%') AND get_action!='/csrf-token' AND "; }
        return "";
    }

    protected class AnalyticsUserLoginsMapper implements RowMapper<AnalyticsUserLogins> {
        @Override
        public AnalyticsUserLogins mapRow(ResultSet rs, int row) throws SQLException {
            AnalyticsUserLogins datum = new AnalyticsUserLogins();
            datum.setUserid(rs.getInt("uid"));
            datum.setUserEmail(rs.getString("email"));
            datum.setLogins(rs.getInt("logins"));
            java.sql.Timestamp lastLogin = rs.getTimestamp("lastLoginDate");
            if (lastLogin!=null){
                datum.setLastLoginDate(lastLogin.toLocalDateTime());
            }
            return datum;
        }

    }

    protected class AnalyticsUserAllLoginsMapper implements RowMapper<AnalyticsUserAllLogins> {
        @Override
        public AnalyticsUserAllLogins mapRow(ResultSet rs, int row) throws SQLException {
            AnalyticsUserAllLogins datum = new AnalyticsUserAllLogins();
            datum.setUserid(rs.getInt("uid"));
            datum.setUserEmail(rs.getString("email"));
            datum.setLogins(rs.getInt("logins"));
            java.sql.Timestamp lastLogin = rs.getTimestamp("lastLoginDate");
            if (lastLogin!=null){
                datum.setLastLoginDate(lastLogin.toLocalDateTime());
            }
            java.sql.Timestamp logDate = rs.getTimestamp("loginDate");
            if (logDate!=null){
                datum.setLoginDate(lastLogin.toLocalDateTime());
            }
            return datum;
        }

    }
}
