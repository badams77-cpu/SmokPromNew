
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
public class REP_UserService extends MajoranaAnnotationRepository<S_User>{

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

    public int getChangePasswordTimeOut() {
        return changePasswordTimeOut;
    }

    public String getTable() {
        return table;
    }

    public S_User getUser(String email) {
        List<S_User> users = this.getByEmail(email);
        S_User user = null;
        if (!users.isEmpty()) {


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
        return user;
    }


    public boolean updateUser(PortalSecurityPrinciple principle,
                              String firstname, String lastname, String email, String language, String organization) {

        // Retrieve the existing user for the principle.
        Optional<S_User> optExistingUser = getById(principle.getId());

        if (optExistingUser.isPresent()) {

            S_User u = optExistingUser.get();
//            u.setFirstname(firstname);
//            u.setLastname(lastname);
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
                        "UPDATE " + table + sql)).count();
                break;
            default:
                rowsAffected = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.update(
                        "UPDATE " + table + sql)).count();

        }
        return rowsAffected == 1;


    }





    public List<S_User> getByEmail(String email) {

        long rowsAffected = 0;

        Object[] inVal = new Object[]{ email };

        S_User searchEntity = new S_User();
        searchEntity.setUsername(email);

        List<List<S_User>> res = new LinkedList<>();

        try {
            switch (dbConnectionFactory.getVariant(dbName)) {

                case CASSANDRA:

                    DBCreds cred = dbEnvSetup.getCreds(dbName);

                    SimpleStatement ss = QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                            .column(CqlIdentifier.fromCql("username")).build(inVal);

                    res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                            .map(templ -> templ.select(ss, S_User.class)).collect(Collectors.toList())
                            .stream().collect(Collectors.toList());

                default:
                    String sql = "SELECT en.*" + UserEmailJoin.getFIELDS() + " FROM " + table + " en " +
                            getTheJoin() +
                            " where en.username= :username";
                    res = dbConnectionFactory.getNamedParameterJdbcTemplate(dbName).stream().map(templ -> templ.query(
                            sql, getSqlParameterSource(searchEntity), getMapper())).collect(Collectors.toList());

            }

        } catch (Exception e){
            LOGGER.warn("Exception e geting User",e);
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

                String sql = "SELECT *"+UserEmailJoin.getFIELDS()+" FROM " + table + " en "+
                        getTheJoin()+
                        " where en.id= ?";

                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        sql, inVal, getMapper())).collect(Collectors.toList());

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

                String sql = "SELECT *"+UserEmailJoin.getFIELDS()+" FROM " + table + " en "+
                        getTheJoin()+
                        " where en.uuid= ?";

                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        sql, inVal, getMapper())).collect(Collectors.toList());

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

        newUser.setUserpw( pwCryptUtil.getPasswd(password, PwCryptUtil.BCryptNum) );
        newUser.setSecVn( PwCryptUtil.BCryptNum);

        long rowsAffected = 0;

        final S_User nu = newUser;

        switch (dbConnectionFactory.getVariant(dbName)) {

            case CASSANDRA:
                String sql = "INSERT INTO " + table + " " + getCreateStringNP(newUser);

//                SimpleStatement cql = SimpleStatement.newInstance(sql);

                rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ -> templ.insert(nu)).count();

                newUser = getByEmail(newUser.getUsername()).stream().findFirst().orElse(null);
                break;
            default:
                String sql1 = "INSERT INTO " + table + " " + getCreateStringNP(newUser);

                Optional<NamedParameterJdbcTemplate> templ = dbConnectionFactory.getNamedParameterJdbcTemplate(dbName);

                SqlParameterSource sps = getSqlParameterSourceWithDeletedAt(dbName, nu);

                rowsAffected = templ.stream().mapToLong(te -> {
                    try {
                        return (long) te.update(sql1, sps,  holder);
                    } catch (Exception e) {
                        LOGGER.error("Error creating record", e);
                        return 0L;
                    }
                }).sum(  );


        Number newUserId = holder.getKey();


        if (newUserId == null || rowsAffected == 0) {
            LOGGER.error("Failed to create rows="+rowsAffected+" newUswrId="+newUserId);
        } else {
            newUser.setId(newUserId.intValue());
        }

    };

        return newUser;

    }

    public int update(S_User sUser){
        int rowsAffected =0 ;
        SqlParameterSource sps = getSqlParameterSource(sUser);
        if (dbFactory.getVariant(dbName)== DatabaseVariant.CASSANDRA){
            String sql = "UPDATE " + table + getUpdateString(sUser);
            rowsAffected = (int) dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ->templ.update(sql)).count();
        } else {
            String sql1 = "UPDATE " + table + getUpdateStringNP(sUser);
            rowsAffected = (int) dbConnectionFactory.getNamedParameterJdbcTemplate(dbName).map(template->template.update(sql1, sps)).stream().count();
            return rowsAffected;

        }
        return rowsAffected;
    }

}
