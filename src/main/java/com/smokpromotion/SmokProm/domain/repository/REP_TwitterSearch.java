package com.smokpromotion.SmokProm.domain.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.smokpromotion.SmokProm.config.DBs.DBCreds;
import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.DBs.DatabaseVariant;
import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.config.common.CassandraState;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.
.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class REP_TwitterSearch {

    extends MajoranaAnnotationRepository<S_User>{

        private final static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

        protected static final String USER_TABLE = "user";


        private int changePasswordTimeOut = 15;

        private DBEnvSetup dbEnvSetup;

        private PwCryptUtil pwCryptUtil;

        private SmokDatasourceName dbName;

        private MajoranaDBConnectionFactory dbConnectionFactory;

        private String DB_NAME = "smok";

        private final String table;

        private UserEmailJoin theJoin;

        // -----------------------------------------------------------------------------------------------------------------
        // Constructors
        // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public REP_UserService(
                DBEnvSetup dbEnvSetup,
                MajoranaDBConnectionFactory dbConnectionFactory,
                PwCryptUtil pwCryptUtil,
                CassandraState cassState,
        @Value("${CHANGE_PASSWORD_EXPIRY_MINS:15}") int changePasswordTimeOut
    ) {
            super(dbConnectionFactory, dbConnectionFactory.getMainDBName() ,S_User.class);
            this.changePasswordTimeOut = changePasswordTimeOut;
            this.dbFactory = dbConnectionFactory;
            this.dbConnectionFactory = dbConnectionFactory;
            this.dbEnvSetup = dbEnvSetup;
            this.pwCryptUtil = pwCryptUtil;
            //this.dbName = dbEnvSetup.getMainDBName();
            this.dbName = dbConnectionFactory.getMainDBName();
            this.table = dbConnectionFactory.getSchemaInDB(dbName)+"."+USER_TABLE;
        }

        public synchronized String getTheJoin(){
            if (theJoin!=null){ return theJoin.getJOIN(); }
            theJoin = new UserEmailJoin(this);
            return theJoin.getJOIN();
        }





    }
