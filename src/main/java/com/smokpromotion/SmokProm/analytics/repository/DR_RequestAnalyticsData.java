package com.smokpromotion.SmokProm.analytics.repository;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.smokpromotion.SmokProm.analytics.AnalyticsSiteEnum;

import com.smokpromotion.SmokProm.analytics.entity.RequestAnalyticsCount;
import com.smokpromotion.SmokProm.analytics.entity.RequestAnalyticsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Profile({"portal","admin","dxpulse_admin"})
@Service
public class DR_RequestAnalyticsData {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_RequestAnalyticsData.class);

    private static final int MAX_REQUEST_BODY_LENGTH=65535;

    private static final int SECONDS_IN_LOGIN = 1800;

    private DbBeanGenericInterface<RequestAnalyticsData> reqRepo = null;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public DR_RequestAnalyticsData() {
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            reqRepo = dBean.getTypedBean(RequestAnalyticsData.class);
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("Class S_User not found");
        }
    }

    public List<RequestAnalyticsData> getByUserAndDates( String email, LocalDate start, LocalDate end, boolean hideInternal){
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        String hideClause = hideInternal(hideInternal);

       String sql = "SELECT * FROM analytics WHERE "+hideClause+" AND email=:email AND (created_at BETWEEN :start AND :end)" +
               " ORDER BY created_at DESC";
        return reqRepo.getBeansNP(sql, new String[]{ "email", "start", "end"}, new Object[]{email, startTime, endTime});
    }

/*
    public Map<String, Integer> getPageViewsByUserAndDates(  String email, LocalDate start, LocalDate end, boolean hideInternal){
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        String hideClause = hideInternal(hideInternal);
        String sql = "SELECT get_action,count(get_action) as cnt FROM analytics WHERE "+hideClause+" site=? AND email=? AND (created_at BETWEEN ? AND ?) GROUP BY get_action";



        List<Pair<String, Integer>> stats =  dbFactory.getJdbcTemplate(portal).map(template->template.query(sql, new Object[]{site.getCode(), email, startTime, endTime}, new RowMapper<Pair<String, Integer>>() {
            public Pair<String, Integer> mapRow(ResultSet rs, int row) throws SQLException {
                return Pair.of(rs.getString("get_action"),rs.getInt("cnt"));
            }
        })).orElse(new LinkedList<>());
        return stats.stream().collect(Collectors.toMap(x->x.getFirst(),x->x.getSecond()));
    }
*/
    public List<RequestAnalyticsData> getByUserAndTimes( String email, LocalDateTime start, LocalDateTime end, boolean hideInternal){
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start);
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end);
        String hideClause = hideInternal(hideInternal);
        String sql = "SELECT * FROM analytics WHERE "+hideClause+" site=? AND  email=:email AND (created_at BETWEEN :start AND :end) ORDER BY created_at DESC";
        return reqRepo.getBeansNP(sql, new String[]{ "email", "start", "end"}, new Object[]{email, startTime, endTime});
    }
    /*
    public Map<String, Integer> getPageViewsByUserAndTime(PortalEnum portal, AnalyticsSiteEnum site, String email, LocalDateTime start, LocalDateTime end, boolean hideInternal){
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start);
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end);
        String hideClause = hideInternal(hideInternal);
        String sql = "SELECT get_action,count(get_action) as cnt FROM analytics WHERE "+hideClause+" site=? AND  email=? AND (created_at BETWEEN ? AND ?) GROUP BY get_action";
        List<Pair<String, Integer>> stats =  dbFactory.getJdbcTemplate(portal).map(template->template.query(sql, new Object[]{site.getCode(), email, startTime, endTime}, new RowMapper<Pair<String, Integer>>(){
            public Pair<String, Integer> mapRow(ResultSet rs, int row) throws SQLException {
                return Pair.of(rs.getString("get_action"),rs.getInt("cnt"));
            }
        })).orElse(new LinkedList<>());
        return stats.stream().collect(Collectors.toMap(x->x.getFirst(),x->x.getSecond()));
    }
*/

    public List<RequestAnalyticsData> getAnalyticsByUserAndDates( LocalDate start, LocalDate end, List<String> emails) throws Exception{

        if (emails.isEmpty()){ return new LinkedList<>(); }
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        String emailTokens = emails.stream().map(x->"?").collect(Collectors.joining(","));
        Object[] params = new Object[2+emails.size()];
        params[0]=startTime;
        params[1]=endTime;
        int insertPos=2;

        for(String email : emails){
            params[insertPos++]=email;
        }
        String sql=
                "SELECT * FROM analytics WHERE site=? AND (created_at BETWEEN ? AND ?) AND email IN ("+emailTokens+") "+

                        " ORDER BY created_at ASC ;\n";
        return reqRepo.getBeans(sql, params);
    }
/*
    public Map<String, Integer> getPageViewsByDates(PortalEnum portal,AnalyticsSiteEnum site, LocalDate start, LocalDate end, boolean hideInternal){
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        String hideClause = hideInternal(hideInternal);
        String sql = "SELECT get_action,count(get_action) as cnt FROM analytics WHERE "+hideClause+ " site=? AND "+
                " email NOT LIKE '%@urcompliant.com' AND email NOT LIKE '%@softwareofexcellence.com' AND email NOT LIKE '%@soeuk.com' AND EMAIL NOT LIKE '%@soeuk2.com'"+
                " AND (created_at BETWEEN ? AND ?) GROUP BY get_action";
        List<Pair<String, Integer>> stats =  dbFactory.getJdbcTemplate(portal).map(template->template.query(sql, new Object[]{site.getCode(), startTime, endTime}, new RowMapper<Pair<String, Integer>>(){
            public Pair<String, Integer> mapRow(ResultSet rs, int row) throws SQLException {
                return Pair.of(rs.getString("get_action"),rs.getInt("cnt"));
            }
        })).orElse(new LinkedList<>());
        return stats.stream().collect(Collectors.toMap(x->x.getFirst(),x->x.getSecond()));
    }
*/
    public List<RequestAnalyticsData> getAnalyticsByUserAndDatesNoApi( LocalDate start, LocalDate end, List<String> emails, boolean hideInternal) throws Exception{

        if (emails.isEmpty()){ return new LinkedList<>(); }
        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));
        String emailTokens = emails.stream().map(x->"?").collect(Collectors.joining(","));

        String hideClause = hideInternal(hideInternal);

        Object[] params = new Object[2+emails.size()];
        params[0]=startTime;
        params[1]=endTime;
        int insertPos=2;
        for(String email : emails){
            params[insertPos++]=email;
        }
        String sql=
                "SELECT * FROM analytics WHERE "+hideClause+" site=? AND (created_at BETWEEN ? AND ?) AND email IN ("+emailTokens+") "+

                        " ORDER BY created_at ASC ;\n";
        return reqRepo.getBeans(sql, params);
    }




/*
    public TreeMap<String, Long> getUsageByPage( String pageStart, LocalDate start, LocalDate end){

        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(start.atTime(0,0));
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(end.atTime(23,59));


        String sql = "SELECT * FROM analytics WHERE site=? AND get_action like ? AND (created_at BETWEEN ? AND ?) ORDER BY created_at DESC";
        final Map<String, Long> unsorted = dbFactory.getJdbcTemplate(portal).map( template->template.query(sql, new Object[]{site.getCode(), pageStart+"%", start, end}, new RequestAnalyticsDataMapper()))
                .map( x->x.stream().map( y->y.getAction()).collect(Collectors.groupingBy( Function.identity(), Collectors.counting())))
                .orElse(new HashMap<>());
        Comparator<String> valueComparator =  new Comparator<String>() {
            public int compare(String k1, String k2) {
                int compare = unsorted.get(k2).compareTo(unsorted.get(k1));
                if (compare == 0) return 1;
                else return compare;
            }
        };
        Collector<? super Map.Entry<String, Long>, ?, ? extends TreeMap<String, Long>> MapEntry = null;
        Set<Map.Entry<String,Long>> entries = unsorted.entrySet();
        TreeMap<String, Long> ret = new TreeMap<>(valueComparator);
        entries.forEach(e-> ret.put(e.getKey(), e.getValue()));
        return ret;
    }

    public List< RequestAnalyticsCount> getGroupUsage(PortalEnum portal, AnalyticsSiteEnum site, int practiceGroupId, String pageStart, LocalDate start, LocalDate end){
        String sql = "SELECT get_action,email,count(created_at) as req_count FROM analytics WHERE site=? AND group_id=? AND get_action like ? AND (created_at BETWEEN ? AND ?) GROUP BY email,get_action ORDER BY email,get_action DESC";
        return dbFactory.getJdbcTemplate(portal).map( template->template.query(sql, new Object[]{site.getCode(), practiceGroupId, pageStart+"%", start, end}, new RequestAnalyticsCountMapper())).orElse(new LinkedList<>());
    }

*/
    public boolean create( RequestAnalyticsData data){
        try {
            return reqRepo.storeBean(data).hasAnyId();
        } catch (Exception e) {
            LOGGER.warn("Exception storing analytics");
            return false;
        }
        //        String sql = dbFactory.translateSQL("INSERT INTO analytics (created_at,updated_at,host,method, uid, email, group_id, group_name, get_action, get_type, get_id, all_get, all_post, duration, agent_string, agent_ip, request_body, site) "
//                                            + " VALUES (now(), now(), :host, :method, :uid, :email, :group_id, :group_name, :get_action, :get_type, :get_id, :all_get, :all_post, :duration, :agent_string, :agent_ip, :request_body, :site); ");
//        return dbFactory.getNamedParameterJdbcTemplate(portal).map( template->template.update(sql,getParameterSource(data))).orElse(0)==1;
    }


 /*----------------------------------------------------------------------------------------------------------------------
 Protected Methods (to allow testing)
 */

    protected class RequestAnalyticsDataMapper implements RowMapper<RequestAnalyticsData> {
        @Override
        public RequestAnalyticsData mapRow(ResultSet rs, int row) throws SQLException {
            RequestAnalyticsData datum = new RequestAnalyticsData();
            datum.setHost(rs.getString("host"));
            datum.setMethod(rs.getString("method"));
            datum.setUid(rs.getInt("uid"));
            datum.setEmail(rs.getString("email"));
            datum.setGroupId(rs.getInt("group_id"));
            datum.setAction(rs.getString("get_action"));
            datum.setQueryString(rs.getString("all_get"));
            datum.setPostedVars(rs.getString("all_post"));
            datum.setDuration(rs.getString("duration"));
            datum.setBrowser(rs.getString("agent_string"));
            datum.setRemoteAddress(rs.getString("agent_ip"));
            datum.setRequestBody(rs.getString("request_body"));
            java.sql.Timestamp date = rs.getTimestamp("created_at");
            datum.setDatetime(date==null ? null : date.toLocalDateTime());
            datum.setSite(AnalyticsSiteEnum.GetFromCode(rs.getString("site")));
            return datum;
        }

    }

    protected class RequestAnalyticsCountMapper implements RowMapper<RequestAnalyticsCount> {
        @Override
        public RequestAnalyticsCount mapRow(ResultSet rs, int row) throws SQLException {
            RequestAnalyticsCount datum = new RequestAnalyticsCount();
            datum.setPage(rs.getString("get_action"));
            datum.setEmail(rs.getString("email"));
            datum.setCount(rs.getInt("req_count"));
            return datum;
        }

    }



    protected SqlParameterSource getParameterSource(RequestAnalyticsData data){
        Map<String,Object> map = new HashMap();
        map.put("host",data.getHost());
        map.put("method", data.getMethod());
        map.put("uid",data.getUid());
        map.put("email", data.getEmail());
        map.put("group_id", data.getGroupId());
        map.put("group_name", data.getGroupName());
        map.put("get_action",data.getAction());
        map.put("get_type","");
        map.put("handler",data.getType());
        map.put("get_id","0");
        map.put("all_get",data.getQueryString());
        map.put("all_post",data.getPostedVars());
        map.put("duration",data.getDuration());
        map.put("agent_string",data.getBrowser());
        map.put("agent_ip",data.getRemoteAddress());
        map.put("request_body",truncate(data.getRequestBody()));
        map.put("site", data.getSite()==null ? null: data.getSite().getCode());
        return new MapSqlParameterSource(map);
    }

    protected String hideInternal(boolean hide){
        if (hide){ return " NOT (get_action LIKE '%/api%') AND get_action!='/csrf-token' AND "; }
        return "";
    }


    private String truncate(String s){
        if (s==null){ return ""; }
        if (s.length()<MAX_REQUEST_BODY_LENGTH){
            return s;
        }
        return s.substring(0,MAX_REQUEST_BODY_LENGTH);
    }




}

