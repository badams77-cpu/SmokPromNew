package com.smokpromotion.SmokProm.domain.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.smokpromotion.SmokProm.config.DBs.DBCreds;
import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.config.common.CassandraState;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class REP_AdminUserService extends MajoranaAnnotationRepository<AdminUser> {

    private final static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_AdminUserService.class);
    
    private static final String ADMIN_TABLE = "admin_user";

    private String ADMIN_DB_NAME = "smok";

    private DBEnvSetup dbEnvSetup;

    private PwCryptUtil pwCryptUtil;

    private SmokDatasourceName dbName;

    private MajoranaDBConnectionFactory dbConnectionFactory;

    private String table;

    @Autowired
    public REP_AdminUserService(
       DBEnvSetup dbEnvSetup,
       MajoranaDBConnectionFactory dbConnectionFactory,
       PwCryptUtil pwCryptUtil,
       CassandraState cassState
    ) {
        super(dbConnectionFactory,  dbConnectionFactory.getMainDBName() , AdminUser.class);
        //this.dbName = dbEnvSetup.getMainDBName();
        this.dbEnvSetup = dbEnvSetup;
        this.pwCryptUtil = pwCryptUtil;
        this.dbConnectionFactory = dbConnectionFactory;
        this.dbName = dbConnectionFactory.getMainDBName();
        this.table = dbConnectionFactory.getSchemaInDB(dbName)+"."+ADMIN_TABLE;
    }

    // MajoranaTODO Change the comment below to reflect this service.

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Used from portal application only. Rely logged in user being a portal user.
    //
    // These methods are used by the portal application where the currently logged in user determines the
    // portal database against which to run queries. The currently logged in portal user also provides the practice
    // group that is used in some methods.
    //
    // This approach helps ensure that the portal user can see only practice potential data associated with their
    // group as well as limit which practices data can be accessed.
    // -----------------------------------------------------------------------------------------------------------------

    public AdminUser getUser(String email) {
        List<AdminUser> users = this.getByEmail(email);
        AdminUser user = null;
        if (!users.isEmpty()) {

            if (users.size() == 1) {

                // A single user of with this email address has been found.
                AdminUser legacyUser = users.get(0);

                if (!legacyUser.isDeleted()) {

                    // The user has been found and is not flagged as deleted.
                    user = legacyUser;

//                    LOGGER.debug("Got userid [" + legacyUser.getUserid() + "] ");

                } else {

                    // The user has been found but is flagged as deleted.
                    LOGGER.debug("getUser: User found but is deleted - " + legacyUser.getIdString());

                }

            }

        }
        return user;
    }


    public boolean updateUser(PortalSecurityPrinciple principle,
                              String firstname, String lastname, String email, String language, String organization) {

        // Retrieve the existing user for the principle.
        Optional<AdminUser> optExistingUser = getById(principle.getId());

        if (optExistingUser.isPresent()) {

            AdminUser u = optExistingUser.get();
            u.setFirstname(firstname);
            u.setLastname(lastname);
            u.setUsername(email);

            boolean successful = updateUser( u);


            return successful;

        }

        return false;

    }

    // MajoranaTODO Change the comment below to reflect this service.

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods - Used from any application - nothing assumed from currently logged in user.
    //
    // Not for direct usage from portal application. Portal application should used the methods above.
    // -----------------------------------------------------------------------------------------------------------------

    public boolean updateUser( AdminUser user) {


            String sql = getUpdateString(user);

            long rowsAffected = 0;

            switch(dbConnectionFactory.getVariant(dbName)) {

                case CASSANDRA:

                    rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(
                            "UPDATE " + table + sql)).count();
                    break;
                default:
                    rowsAffected = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.update(
                    "UPDATE " + table + sql)).count();

            }
            return rowsAffected == 1;


    }





    public List<AdminUser> getByEmail(String email) {

        long rowsAffected = 0;

        Object[] inVal = new Object[]{ email };

        List<List<AdminUser>> res = new LinkedList<>();

                switch(dbConnectionFactory.getVariant(dbName)) {

                    case CASSANDRA:

                        DBCreds cred = dbEnvSetup.getCreds(dbName);

                        SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                                .column(CqlIdentifier.fromCql("username")).build(inVal);

                        res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                                .map(templ->templ.select(ss, AdminUser.class)).collect(Collectors.toList())
                                .stream().collect(Collectors.toList());

                    default:
                        res = dbConnectionFactory.getJdbcTemplate(dbName).stream()
                                .map(templ->templ.query(
                                "SELECT * FROM " + table + " where username=?", inVal, getMapper())).collect(Collectors.toList());

                }
        return res.stream().flatMap( s -> s.stream() ).collect(Collectors.toList());

    }





    public Optional<AdminUser> getById( int id) {

        Object[] inVal = new Object[]{ id };


        List<List<AdminUser>> res = new LinkedList<>();

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                DBCreds cred = dbEnvSetup.getCreds(dbName);

                SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                        .column(CqlIdentifier.fromCql("id")).build(inVal);

                res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                        .map(templ->templ.select(ss, AdminUser.class)).collect(Collectors.toList())
                        .stream().collect(Collectors.toList());

            default:
                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        "SELECT " + table + " where username=", inVal, getMapper())).collect(Collectors.toList());

        }
        return res.stream().flatMap( s -> s.stream() ).findFirst();

    }

    public Optional<AdminUser> getByUuid( UUID id) {

        Object[] inVal = new Object[]{ id };


        List<List<AdminUser>> res = new LinkedList<>();

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                DBCreds cred = dbEnvSetup.getCreds(dbName);

                SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                        .column(CqlIdentifier.fromCql("id")).build(inVal);

                res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                        .map(templ->templ.select(ss, AdminUser.class)).collect(Collectors.toList())
                        .stream().collect(Collectors.toList());

            default:
                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        "SELECT " + table + " where username=", inVal, getMapper())).collect(Collectors.toList());

        }
        return res.stream().flatMap( s -> s.stream() ).findFirst();

    }



    public boolean delete(int id) {
        Optional<AdminUser> adminUser = getById(id);
        if (!adminUser.isPresent()){ return false; }
        String newName= adminUser.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        AdminUser u = adminUser.get();
        u.setDeleted(true);
        u.setDeletedAt(LocalDateTime.now());
        u.setUsername(newName);
        return updateUser(u);
    }

    public boolean delete(UUID id) {
        Optional<AdminUser> adminUser = getByUuid(id);
        if (!adminUser.isPresent()){ return false; }
        String newName= adminUser.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        AdminUser u = adminUser.get();
        u.setDeleted(true);
        u.setDeletedAt(LocalDateTime.now());
        u.setUsername(newName);
        return updateUser(u);
    }

    public boolean delete(AdminUser u){
        switch(dbConnectionFactory.getVariant(dbName)) {
            case CASSANDRA:
                return delete(u.getUuid());
            default:
                return delete(u.getId());
        }
    }

    public boolean changePassword(int id, String neww) {

        Optional<AdminUser> adminUser = getById(id);
        if (!adminUser.isPresent()){ return false; }
        AdminUser u = adminUser.get();
        u.setUsername(neww);
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);
    }

    public boolean changePassword(UUID id, String neww) {

        Optional<AdminUser> adminUser = getByUuid(id);
        if (!adminUser.isPresent()){ return false; }
        AdminUser u = adminUser.get();
        u.setUsername(neww);
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);
    }

    private boolean changePasswordToBCrypt( int id, String neww) {

        Optional<AdminUser> adminUser = getById(id);
        if (!adminUser.isPresent()){ return false; }
        String newName= adminUser.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        AdminUser u = adminUser.get();
        u.setSecVn(SecVnEnum.BCRYPT.getCode());
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);

    }

    public boolean isPasswordGood(AdminUser u, String password) {
        return pwCryptUtil.isPasswordGood(u.getSecVn(), u.getUsername(), password, u.getUserpw() );
    }

    public AdminUser create( AdminUser newUser, String password) throws SQLException {

        KeyHolder holder = new GeneratedKeyHolder();

        String sql = getCreateString(newUser);

        final AdminUser nu = newUser;

        long rowsAffected = 0;

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(
                        "INSERT INTO " + ADMIN_TABLE + sql)).count();

                newUser = getByEmail(newUser.getUsername()).stream().findFirst().orElse(null);
            break;
            default:

                    rowsAffected = dbConnectionFactory.getJdbcTemplate(dbName).stream().mapToLong(templ -> {
                            try {
                             return templ.update(getSqlPreparedStatementParameter(sql, nu), holder);
                            } catch (SQLException e){
                                LOGGER.error("Exception creating new Admin User",e);
                                return 0;
                            }}).sum();

                    Number newUserId = holder.getKey();

                    if (newUserId == null) {
                        LOGGER.error("Failed to create rows=" + rowsAffected + " newUswrId=" + newUserId);
                    } else {
                        newUser.setId(newUserId.intValue());
                    }
                    

        };

        return newUser;

    }



    
}
