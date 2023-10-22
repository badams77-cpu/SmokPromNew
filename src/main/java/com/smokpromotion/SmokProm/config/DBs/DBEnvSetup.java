package com.smokpromotion.SmokProm.config.DBs;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.smokpromotion.SmokProm.config.common.YamlDBConfig;
import com.smokpromotion.SmokProm.config.common.YamlPropertySourceFactory;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DBEnvSetup {

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(DBEnvSetup.class);

    private final static String[] CredFields = DBCreds.getCredFields();

    private static final String PREFIX = YamlDBConfig.getPrefix();

    @Autowired
    private Environment env;

    @Autowired
    private YamlDBConfig ydb;

    private List<DBCreds> envCredList;

    private  Map<SmokDatasourceName, DBCreds> envCredMap;

    private  Map<SmokDatasourceName, HikariDataSource> dataSources;

    private  Map<SmokDatasourceName, SmokDataSource> smokDataSourceMap;

    private  Map<SmokDatasourceName, CqlSession> smokCassMap;

    public static Map<String, Object> getAllKnownProperties(Environment env) {
        Map<String, Object> rtn = new HashMap<>();
        if (env instanceof ConfigurableEnvironment) {
            for (PropertySource<?> ps : ((ConfigurableEnvironment) env).getPropertySources()   ) {
                if (ps instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) ps).getPropertyNames()) {
                        rtn.put(key, ((EnumerablePropertySource<?>) ps).getProperty(key));
                    }
                }
            }
        }
        return rtn;
    }

    public static <T> List<T> withoutFirst(List<T> o) {
        final List<T> result = new ArrayList<T>();
        for (int i = 1; i < o.size(); i++)
            result.add(o.get(i));

        return result;
    }

    @Autowired
    public DBEnvSetup(YamlDBConfig ydb, Environment env){
        this.env = env;
        this.ydb = ydb;

        Map<String, Object> allProp = getAllKnownProperties(env);

        Map<List<String>, Object> splitKeys = allProp.entrySet().stream()
                .collect( Collectors.toMap(
                    x->Arrays.asList( ((String) x.getKey()).split("\\.")),
                        Map.Entry::getValue,
                   (a,b)->a)
                );

        Map<List<String>, Object> withPrefix = splitKeys.entrySet().stream()
                .filter( x->x.getKey().size()>1 && x.getKey().get(0).equalsIgnoreCase(PREFIX) )
                .collect( Collectors.toMap(x->withoutFirst(x.getKey()), Map.Entry::getValue));

        Map<String, Map<String, String>> propsByDb = withPrefix.entrySet().stream()
                .filter( x->x.getKey().size()>1 )
                .collect( Collectors.groupingBy( x-> ((Map.Entry<List<String>, Object>) x).getKey().get(0),
                        Collectors.toMap( y-> String.join(".",
                                        withoutFirst(((Map.Entry<List<String>, Object>) y).getKey())),
                                z->((Map.Entry<List<String>, Object>) z).getValue().toString())
                        )
                );

        envCredMap = new HashMap<>();
        dataSources = new HashMap<>();
        smokDataSourceMap = new HashMap<>();
        smokCassMap = new HashMap<>();
        envCredList = new LinkedList<>();
        Map<String, Object> map = new HashMap<>();
        for(Iterator<Map.Entry<String, Map<String, String>>> it = propsByDb.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Map<String, String>> entry = it.next();
            String topKey = entry.getKey();
            Map<String, String> propertySource = entry.getValue();
            for (String key : propertySource.keySet()) {
                String longKey = PREFIX + "."+ topKey + "." + key;
                String envValue = env.getProperty(longKey);
                String val = envValue==null ? propertySource.get(key) : envValue;
                map.put(longKey, val);
            }
        }
        LOGGER.warn("Read "+map.size()+" env and property items");

        Map<String, Object> dbEnvMap = allProp.entrySet().stream().filter( en->en.getKey().startsWith(PREFIX+".")).collect(Collectors.toMap(en->en.getKey().substring(PREFIX.length()+1),en->en.getValue()));

        Set<String> names =  dbEnvMap.keySet().stream().map(x->x.substring(0,x.indexOf("."))).collect(Collectors.toSet());

        LOGGER.warn("Read "+names.size()+" DBs to connect to");

        for(String smkDS : names){
            SmokDatasourceName sdsn = new SmokDatasourceName(smkDS);
            Map<String, Object> credData =
                    dbEnvMap.entrySet().stream().filter( en->en.getKey().startsWith(sdsn.getDataSourceName()))
                            .collect(Collectors.toMap( x->x.getKey(), y->y.getValue()));
            int found = 0;

            LOGGER.warn("Seting up source "+sdsn);

            Map<String, String> credMap = new HashMap<>();
            String stem = PREFIX+"."+smkDS+".";

            for(String field : CredFields) {
                String key = smkDS +"."+ field;
                Object oVal = credData.get(key);

                String value = "";
                if (oVal == null) {
                    LOGGER.warn("Missing DB Set Env Var: " + key);
                    continue;
                } else {
                    if (oVal instanceof Number ) {
                        long n = ((Number) oVal).longValue();
                        value = "" + n;
                    } else if (oVal instanceof Boolean) {
                        value = ((Boolean) oVal).toString();
                    } else {
                        value = (String) credData.get(key);
                    }
                }
                found++;
                credMap.put(field, value);
            }
            if (found >= CredFields.length) {
                try {
                    DBCreds creds = new DBCreds(credMap);
                    envCredMap.put(sdsn, creds);
                    envCredList.add(creds);
                    SmokDataSource src = new SmokDataSource(creds);
                    smokDataSourceMap.put(sdsn, src);
                    if (creds.getVariant() == DatabaseVariant.CASSANDRA) {
                        CassandraConnector cassConn = new CassandraConnector();
                        cassConn.connect(creds);

                        CqlSession cqlSess = cassConn.getSession();
                        if (cqlSess==null){
                            LOGGER.error("Error get CqlSession "+sdsn+" "+cassConn.getMessage());
                            continue;
                        }
                        cqlSess.execute("USE " + CqlIdentifier.fromCql(creds.getName().getDataSourceName()));
                        ;
                        smokCassMap.put(sdsn, cqlSess);
                    } else {
                        HikariDataSource hikariDataSource = src.getHikariDataSource();
                        dataSources.put(sdsn, hikariDataSource);
                    }
                } catch (Exception e){
                    LOGGER.error("Error create datasource "+sdsn,e);
                }
            } else {
                String missing = Arrays.asList(CredFields).stream().map( k-> smkDS+"."+k)
                        .filter( k->credMap.containsKey(k)).collect(Collectors.joining(", "));
                LOGGER.error("DB Config: " + sdsn + " missing fields "+missing);
            }
        }
    }

    public DBCreds getCreds(SmokDatasourceName dbName){
        return envCredMap.getOrDefault(dbName, new DBCreds(new HashMap<>()));
    }

    public HikariDataSource getHikDatasource(SmokDatasourceName dbSrcName)
    {
        return dataSources.get(dbSrcName);
    }

    public SmokDataSource getSmokDatasource(SmokDatasourceName dbSrcName){
        return smokDataSourceMap.get(dbSrcName);
    }

    public CqlSession getCqlSession(SmokDatasourceName dbSrcName){
        return smokCassMap.get(dbSrcName);
    }

    public SmokDatasourceName getMainCassDBName(){
        return envCredList.stream().filter(x-> x.getVariant()== DatabaseVariant.CASSANDRA)
                .findFirst().orElse(new DBCreds()).getName()
        ;
    }

    public SmokDatasourceName getMainSqlDBName(){
        return envCredList.stream().filter(x->x.getVariant() !=DatabaseVariant.CASSANDRA)
                .findFirst().orElse(new DBCreds()).getName()
                ;
    }

    public SmokDatasourceName getMainDBName(){
        return envCredList.stream()
                .findFirst().orElse(new DBCreds()).getName()
                ;
    }
}
