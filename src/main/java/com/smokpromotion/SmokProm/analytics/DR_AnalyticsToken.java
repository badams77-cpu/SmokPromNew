package com.smokpromotion.SmokProm.analytics;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Lazy
@Service
public class DR_AnalyticsToken {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_AnalyticsToken.class);
    private DbBeanGenericInterface<DE_EmailTemplate> emailRepo = null;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public DR_AnalyticsToken(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            emailRepo = dBean.getTypedBean(DE_EmailTemplate.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }
    private static final String TABLE = "analytics_token";

    @Autowired
    public DR_AnalyticsToken(MPCAppDBConnectionFactory dbFactory){
        this.dbFactory = dbFactory;
    }

    public boolean save(PortalEnum portal, LocalDate date, UUID token){
        String sql = "insert into "+TABLE+" (token_date,token) values (:token_date, :token) ";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("token_date", java.sql.Date.valueOf(date));
        paramMap.put("token", token.toString());
        return dbFactory.getNamedParameterJdbcTemplate(portal).map(template->template.update(sql, new MapSqlParameterSource(paramMap))).orElse(0)==1;
    }

    public UUID getForDate(PortalEnum portal, LocalDate date){
        String sql = "select * FROM "+TABLE+" where token_date=?";
        List<String> uuids = dbFactory.getJdbcTemplate(portal).map(template->template.query(sql, new Object[]{ java.sql.Date.valueOf(date)},  new TokenRowMapper())).orElse(new LinkedList<>());
        if (uuids.isEmpty()){ return null; }
        try {
            return UUID.fromString(uuids.get(0));
        } catch (Exception e){
            LOGGER.error("getForDate: Analytics token "+uuids.get(0)+" was not a uuid");
            return null;
        }
    }

    public boolean deleteOld(PortalEnum portal, LocalDate date){
        String sql = "DELETE FROM "+TABLE+" where token_date<:token_date";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("token_date", java.sql.Date.valueOf(date));
        return dbFactory.getNamedParameterJdbcTemplate(portal).map(template->template.update(sql, new MapSqlParameterSource(paramMap))).orElse(0)>0;
    }

    protected class TokenRowMapper implements RowMapper<String> {

        @Override
        public String mapRow(ResultSet rs, int row) throws SQLException {
            return rs.getString("token");
        }
    }

}

