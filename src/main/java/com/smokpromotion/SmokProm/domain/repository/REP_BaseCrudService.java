package com.smokpromotion.SmokProm.domain.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.smokpromotion.SmokProm.config.DBs.DBCreds;
import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.config.common.CassandraState;
import com.smokpromotion.SmokProm.domain.entity.BaseSmokEntity;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.smokpromotion.SmokProm.util.PwCryptUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public abstract class REP_BaseCrudService<T extends BaseSmokEntity>  {

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

    @Autowired
    private REP_UserService userService;

    private MajoranaDBConnectionFactory majoranaDBConnectionFactory;

    private MajoranaAnnotationRepository<T> repository;

    private Class clazz;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public REP_BaseCrudService(
            DBEnvSetup dbEnvSetup,
            MajoranaDBConnectionFactory dbConnectionFactory,
            CassandraState cassState
    ) {
        this.clazz = (new T()).getClass();
        this.changePasswordTimeOut = changePasswordTimeOut;
        this.dbConnectionFactory = dbConnectionFactory;
        this.dbEnvSetup = dbEnvSetup;
        this.pwCryptUtil = pwCryptUtil;
        //this.dbName = dbEnvSetup.getMainDBName();
        this.dbName = dbConnectionFactory.getMainDBName();
        this.table = dbConnectionFactory.getSchemaInDB(dbName)+"."+getTableName();
        this.repository = new MajoranaAnnotationRepository<T>(dbConnectionFactory, dbName, (new T()).getClass());
    }

    protected abstract String getTableName();

    public synchronized String getTheJoin(){
        if (theJoin!=null){ return theJoin.getJOIN(); }
        theJoin = new UserEmailJoin(userService);
        return theJoin.getJOIN();
    }



    public List<T> getByStringField(String field, String email) {

        long rowsAffected = 0;

        Object[] inVal = new Object[]{ email };

        T searchEntity = new T();

        majoranaDBConnectionFactory.

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

    public SqlParameterSource getSqlParameterSource(SmokDatasourceName dbName, T entity){
        return repository.getSqlParameterSource( entity);
    }

    public SqlParameterSource getSqlParameterSourceWithDeletedAt(SmokDatasourceName dbName, T entity){
        return repository.getSqlParameterSourceWithDeletedAt(dbName, entity);
    }

    public R owMapper<T> getMapper(){
        return repository.getMapper();
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

    public Optional<T> getByUuid( UUID id) {

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

}
