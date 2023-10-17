package com.smokpromotion.SmokProm.domain.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.smokpromotion.SmokProm.config.DBs.DBCreds;
import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.DBs.DatabaseVariant;
import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class REP_UserService extends MajoranaAnnotationRepository<S_User>{

    private final static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_UserService.class);

    private static final String ADMIN_TABLE = "users";

    private int changePasswordTimeOut = 15;

    private DBEnvSetup dbEnvSetup;

    private PwCryptUtil pwCryptUtil;

    private SmokDatasourceName dbName;

    private MajoranaDBConnectionFactory dbConnectionFactory;
    
    private static final String USER_TABLE = "users";

    private static final String DB_NAME = "smok";
    
    private final String table;
    
    
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public REP_UserService(
            DBEnvSetup dbEnvSetup,
            MajoranaDBConnectionFactory dbConnectionFactory,
            PwCryptUtil pwCryptUtil,
            @Value("${CHANGE_PASSWORD_EXPIRY_MINS:15}") int changePasswordTimeOut
    ) {
        super(dbConnectionFactory, S_User.class);
        this.changePasswordTimeOut = changePasswordTimeOut;
        this.dbFactory = dbConnectionFactory;
        this.dbConnectionFactory = dbConnectionFactory;
        this.dbEnvSetup = dbEnvSetup;
        this.pwCryptUtil = pwCryptUtil;
        this.table = USER_TABLE;
        this.dbName = new SmokDatasourceName(DB_NAME);
    }

    public int getChangePasswordTimeOut() {
        return changePasswordTimeOut;
    }

    public S_User getUser(String email) {
        List<S_User> users = this.getByEmail(email);
        S_User user = null;
        if (!users.isEmpty()) {

            if (users.size() == 1) {

                // A single user of with this email address has been found.
                S_User legacyUser = users.get(0);

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
        Optional<S_User> optExistingUser = getById(principle.getId());

        if (optExistingUser.isPresent()) {

            S_User u = optExistingUser.get();
            u.setFirstname(firstname);
            u.setLastname(lastname);
            u.setUsername(email);

            boolean successful = updateUser( principle, firstname, lastname, email, language, organization);

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

    public boolean updateUser( S_User user) {


        String sql = getUpdateString(user);

        long rowsAffected = 0;

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(
                        "UPDATE " + ADMIN_TABLE + sql)).count();
                break;
            default:
                rowsAffected = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.update(
                        "UPDATE " + ADMIN_TABLE + sql)).count();

        }
        return rowsAffected == 1;


    }





    public List<S_User> getByEmail(String email) {

        long rowsAffected = 0;

        Object[] inVal = new Object[]{ email };

        List<List<S_User>> res = new LinkedList<>();

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                DBCreds cred = dbEnvSetup.getCreds(dbName);

                SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                        .column(CqlIdentifier.fromCql("username")).build(inVal);

                res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                        .map(templ->templ.select(ss, S_User.class)).collect(Collectors.toList())
                        .stream().collect(Collectors.toList());

            default:
                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        "SELECT " + ADMIN_TABLE + " where username=", inVal, getMapper())).collect(Collectors.toList());

        }
        return res.stream().flatMap( s -> s.stream() ).collect(Collectors.toList());

    }





    public Optional<S_User> getById( int id) {

        Object[] inVal = new Object[]{ id };


        List<List<S_User>> res = new LinkedList<>();

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                DBCreds cred = dbEnvSetup.getCreds(dbName);

                SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                        .column(CqlIdentifier.fromCql("id")).build(inVal);

                res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                        .map(templ->templ.select(ss, S_User.class)).collect(Collectors.toList())
                        .stream().collect(Collectors.toList());

            default:
                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        "SELECT " + ADMIN_TABLE + " where username=", inVal, getMapper())).collect(Collectors.toList());

        }
        return res.stream().flatMap( s -> s.stream() ).findFirst();

    }

    public Optional<S_User> getByUuid( UUID id) {

        Object[] inVal = new Object[]{ id };

        List<List<S_User>> res = new LinkedList<>();

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                DBCreds cred = dbEnvSetup.getCreds(dbName);

                SimpleStatement ss =  QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                        .column(CqlIdentifier.fromCql("id")).build(inVal);

                res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                        .map(templ->templ.select(ss, S_User.class)).collect(Collectors.toList())
                        .stream().collect(Collectors.toList());

            default:
                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        "SELECT " + ADMIN_TABLE + " where username=", inVal, getMapper())).collect(Collectors.toList());

        }
        return res.stream().flatMap( s -> s.stream() ).findFirst();

    }



    public boolean delete(int id) {
        Optional<S_User> S_User = getById(id);
        if (!S_User.isPresent()){ return false; }
        String newName= S_User.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        S_User u = S_User.get();
        u.setDeleted(true);
        u.setDeletedAt(LocalDateTime.now());
        u.setUsername(newName);
        return updateUser(u);
    }

    public boolean delete(UUID id) {
        Optional<S_User> S_User = getByUuid(id);
        if (!S_User.isPresent()){ return false; }
        String newName= S_User.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        S_User u = S_User.get();
        u.setDeleted(true);
        u.setDeletedAt(LocalDateTime.now());
        u.setUsername(newName);
        return updateUser(u);
    }

    public boolean delete(S_User u){
        switch(dbConnectionFactory.getVariant(dbName)) {
            case CASSANDRA:
                return delete(u.getUuid());
            default:
                return delete(u.getId());
        }
    }

    public boolean changePassword(int id, String neww) {

        Optional<S_User> S_User = getById(id);
        if (!S_User.isPresent()){ return false; }
        S_User u = S_User.get();
        u.setUsername(neww);
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);
    }

    public boolean changePassword(UUID id, String neww) {

        Optional<S_User> S_User = getByUuid(id);
        if (!S_User.isPresent()){ return false; }
        S_User u = S_User.get();
        u.setUsername(neww);
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);
    }

    private boolean changePasswordToBCrypt( int id, String neww) {

        Optional<S_User> S_User = getById(id);
        if (!S_User.isPresent()){ return false; }
        String newName= S_User.map(x->"DEL:"+x.getId()+":"+x.getUsername()).orElse("");
        S_User u = S_User.get();
        u.setSecVn(SecVnEnum.BCRYPT.getCode());
        u.setUserpw(pwCryptUtil.getPasswd(neww, u.getSecVn()));
        return updateUser(u);

    }

    public boolean isPasswordGood(S_User u, String password) {
        return pwCryptUtil.isPasswordGood(u.getSecVn(), u.getUsername(), password, u.getUserpw() );
    }

    public S_User create( S_User newUser, String password) {

        KeyHolder holder = new GeneratedKeyHolder();

        String sql = getCreateString(newUser);

        long rowsAffected = 0;

        switch(dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:

                rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(
                        "INSERT INTO " + ADMIN_TABLE + sql)).count();

                newUser = getByEmail(newUser.getUsername()).stream().findFirst().orElse(null);
                break;
            default:
                rowsAffected = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->
                        templ.update(  sql, getMapper())).count();


                Number newUserId = holder.getKey();

                if (newUserId==null){
                    LOGGER.error("Trying to create User without portal Database present");
                } else {
                    newUser.setId(newUserId.intValue());
                }

        };

        return newUser;

    }

    public int update(S_User sUser){
        int rowsAffected =0 ;
        if (dbFactory.getVariant(dbName)== DatabaseVariant.CASSANDRA){
            rowsAffected = (int) dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(
                    "UPDATE " + table + getUpdateString(sUser))).count();
        } else {
            rowsAffected = (int) dbConnectionFactory.getJdbcTemplate(dbName).map(template->template.update(
                    "UPDATE "+table + getUpdateString(sUser))).stream().count();
            return rowsAffected;

        }
        return rowsAffected;
    }

}
