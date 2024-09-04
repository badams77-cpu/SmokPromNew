package com.smokpromotion.SmokProm.scheduler.repository;

import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.repository.MPCAppDBConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DR_SchedulePermissions {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_SchedulePermissions.class);
    private static final String TABLE = "schedule_rpt_permission";

    private final MPCAppDBConnectionFactory dbFactory;
    private final boolean restrictPermission;

    @Autowired
    public DR_SchedulePermissions (
            MPCAppDBConnectionFactory dbFactory,
            @Value("${MPC_SCHEDULE_RPTS_RESTRICT:false}") String restrictPermission
  ) {
        this.dbFactory = dbFactory;
        this.restrictPermission = Boolean.valueOf(restrictPermission);
    }

    public boolean isSchedulingPermitted(PortalSecurityPrinciple principle){

        if (this.restrictPermission) {
            PortalEnum portal = principle.getPortal();
            String sql = "SELECT PracticeGroupID FROM " + TABLE + " WHERE userid IS NULL AND PracticeGroupID=:group\n" +
                    " UNION\n" +
                    "SELECT PracticeGroupID FROM " + TABLE + " WHERE userid=:userid AND PracticeGroupID=:group\n";
            List<Integer> res = dbFactory.getNamedParameterJdbcTemplate(portal)
                    .map(template -> template.query(sql, getSqlParameterSource(principle), new SingleColumnRowMapper<Integer>(Integer.class))).orElse(new LinkedList<>());
            return !res.isEmpty();
        }

        // no restriction
        return true;
    }

    protected SqlParameterSource getSqlParameterSource(PortalSecurityPrinciple principle){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("userid",principle.getId());
        params.put("group",principle.getPracticeGroupId());
        return new MapSqlParameterSource(params);
    }



}


