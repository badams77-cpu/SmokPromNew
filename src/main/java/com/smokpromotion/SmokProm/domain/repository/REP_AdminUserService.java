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
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import com.smokpromotion.SmokProm.util.SecVnEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private REP_UserService userService;

    private UserEmailJoin theJoin;
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

    public synchronized String getTheJoin(){

        if (theJoin!=null){ return theJoin.getJOIN(); }
        theJoin = new UserEmailJoin(userService);
        return theJoin.getJOIN();
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

            long rowsAffected = 0;

            final AdminUser au = user;

            switch(dbConnectionFactory.getVariant(dbName)) {

                case CASSANDRA:

//                SimpleStatement cql = SimpleStatement.newInstance(sql);

                    rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ -> templ.update(au)).count();

                    user = getByEmail(user.getUsername()).stream().findFirst().orElse(null);
                    break;

                default:

                    String sql1 = "UPDATE " + table + getUpdateString(user);

                    Optional<JdbcTemplate> templ = dbConnectionFactory.getJdbcTemplate(dbName);
                    rowsAffected = templ.stream().mapToLong(te -> {
                        try {

                            return (long) te.update( getSqlPreparedStatementParameter(sql1, au, true));
                        } catch (Exception e) {
                            LOGGER.error("Error creating record", e);
                            return 0L;
                        }
                    }).sum(  );

            }
            return rowsAffected == 1;


    }


    public List<AdminUser> getByEmail(String email) {

        long rowsAffected = 0;

        Object[] inVal = new Object[]{ email };

        AdminUser searchEntity = new AdminUser();
        searchEntity.setUsername(email);


        List<List<AdminUser>> res = new LinkedList<>();

        try {

            switch (dbConnectionFactory.getVariant(dbName)) {

                case CASSANDRA:

                    DBCreds cred = dbEnvSetup.getCreds(dbName);

                    SimpleStatement ss = QueryBuilder.selectFrom(dbName.getDataSourceName(), cred.getGroup())
                            .column(CqlIdentifier.fromCql("username")).build(inVal);

                    res = dbConnectionFactory.getCassandraTemplate(dbName).stream()
                            .map(templ -> templ.select(ss, AdminUser.class)).collect(Collectors.toList())
                            .stream().collect(Collectors.toList());

                default:

                    String sql = "SELECT *" + UserEmailJoin.getFIELDS() + " FROM " + table + " en " +
                            getTheJoin() +
                            " where en.username= :username";

                    res = dbConnectionFactory.getNamedParameterJdbcTemplate(dbName).stream()
                            .map(templ -> templ.query(sql, getSqlParameterSource(searchEntity),
                                    getMapper())).collect(Collectors.toList());
            }

        } catch (Exception e){
            LOGGER.warn("Exception getting adminUser ",e);
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

                String sql = "SELECT *"+UserEmailJoin.getFIELDS()+" FROM " + table +
                        getTheJoin()+
                        " en where en.id = ?";

                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        sql , inVal, getMapper())).collect(Collectors.toList());

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

                String sql = "SELECT *"+UserEmailJoin.getFIELDS()+" FROM " + table +
                        getTheJoin()+
                        " en where en.uuid= ?";

                res = dbConnectionFactory.getJdbcTemplate(dbName).stream().map(templ->templ.query(
                        sql , inVal, getMapper())).collect(Collectors.toList());

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

        final AdminUser nu = newUser;

        nu.setSecVn(SecVnEnum.BCryptNum);
        nu.setUserpw(pwCryptUtil.getPasswd(password, SecVnEnum.BCryptNum));

        long rowsAffected = 0;

       try {

           switch (dbConnectionFactory.getVariant(dbName)) {

               case CASSANDRA:
                   String sql = "INSERT INTO " + table + " " + getCreateStringNP(newUser);

                   rowsAffected = dbConnectionFactory.getCassandraTemplate(dbName).stream().map(templ -> templ.update(
                           sql)).count();

                   newUser = getByEmail(newUser.getUsername()).stream().findFirst().orElse(null);
                   break;
               default:
                   String sql1 = "INSERT INTO " + table + " " + getCreateStringNP(newUser);

                   rowsAffected = dbConnectionFactory.getNamedParameterJdbcTemplate(dbName).stream().mapToLong(templ -> {
                       try {
                           return templ.update(sql1,
                                   getSqlParameterSource(nu), holder);
                       } catch (DataAccessException e) {
                           LOGGER.error("Exception creating new Admin User", e);
                           return 0;
                       }
                   }).sum();

                   Number newUserId = holder.getKey();

                   if (newUserId == null) {
                       LOGGER.error("Failed to create rows=" + rowsAffected + " newUswrId=" + newUserId);
                   } else {
                       newUser.setId(newUserId.intValue());
                   }


           }
           ;

       } catch (Exception e){
           LOGGER.warn("Insert Admin User, Exception: ",e );
       }

        return newUser;

    }



    
}
