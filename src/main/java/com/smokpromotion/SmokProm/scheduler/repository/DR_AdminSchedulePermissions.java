package com.smokpromotion.SmokProm.scheduler.repository;

import com.urcompliant.config.admin.AdminSecurityPrinciple;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Profile("admin")
public class DR_AdminSchedulePermissions {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_AdminSchedulePermissions.class);
    private static final String TABLE = "admin_schedule_rpt_permission";

    private final MPCAppDBConnectionFactory dbFactory;
    private final boolean restrictPermission;

    @Autowired
    public DR_AdminSchedulePermissions(
            MPCAppDBConnectionFactory dbFactory,
            @Value("${MPC_ADMIN_SCHEDULE_RPTS_RESTRICT:false}") String restrictPermission
  ) {
        this.dbFactory = dbFactory;
        this.restrictPermission = Boolean.valueOf(restrictPermission);
    }

    public boolean isSchedulingPermitted(AdminSecurityPrinciple principle){

        if (this.restrictPermission) {
            PortalEnum portal = PortalEnum.AWS;
            String sql = "SELECT * FROM " + TABLE + " WHERE userid=:userid\n";
            List<Integer> res = dbFactory.getNamedParameterJdbcTemplate(portal)
                    .map(template -> template.query(sql, getSqlParameterSource(principle), new SingleColumnRowMapper<Integer>(Integer.class))).orElse(new LinkedList<>());
            return !res.isEmpty();
        }

        // no restriction
        return true;
    }

    protected SqlParameterSource getSqlParameterSource(AdminSecurityPrinciple principle){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("userid",principle.getId());
        return new MapSqlParameterSource(params);
    }



}


