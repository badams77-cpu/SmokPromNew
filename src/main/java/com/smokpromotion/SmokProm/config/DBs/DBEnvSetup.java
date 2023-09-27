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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DBEnvSetup {

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SmokDataSource.class);

    private final static String[] CredFields = DBCreds.getCredFields();

    private static final String PREFIX = YamlDBConfig.getPrefix();

    @Autowired
    private Environment env;

    @Autowired private YamlDBConfig ydb;

    private List<DBCreds> envCredList;

    private final Map<SmokDatasourceName, DBCreds> envCredMap;

    private final Map<SmokDatasourceName, HikariDataSource> dataSources;

    private final Map<SmokDatasourceName, SmokDataSource> smokDataSourceMap;

    private final Map<SmokDatasourceName, CqlSession> smokCassMap;

    @Autowired
    public DBEnvSetup(YamlDBConfig ydb, Environment env){
        this.env = env;
        this.ydb = ydb;
        envCredMap = new HashMap<>();
        dataSources = new HashMap<>();
        smokDataSourceMap = new HashMap<>();
        smokCassMap = new HashMap<>();
        envCredList = new LinkedList<>();
        Map<String, Object> map = new HashMap<>();
        for(Iterator<Map.Entry<String, Map<String, String>>> it = ydb.getEntries().entrySet().iterator(); it.hasNext(); ) {
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

        Map<String, Object> dbEnvMap = map.entrySet().stream().filter( en->en.getKey().startsWith(PREFIX+".")).collect(Collectors.toMap(en->en.getKey().substring("smokDb.".length()),en->en.getValue()));

        Set<String> names =  dbEnvMap.keySet().stream().map(x->x.substring(0,x.indexOf("_"))).collect(Collectors.toSet());

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
                String key = stem + field;
                String value = (String) credData.get(key);
                if (value == null) {
                    LOGGER.warn("Missing DB Set Env Var: " + key);
                    continue;
                }
                found++;
                credMap.put(field, value);
            }
            if (found == CredFields.length) {
                DBCreds creds = new DBCreds(credMap);
                envCredMap.put(sdsn, creds);
                envCredList.add(creds);
                SmokDataSource src = new SmokDataSource(creds);
                smokDataSourceMap.put(sdsn, src);
                if (creds.getVariant()!=DatabaseVariant.CASSANDRA){
                    CassandraConnector cassConn = new CassandraConnector();
                    cassConn.connect(creds);

                  CqlSession cqlSess = cassConn.getSession();
                  cqlSess.execute("USE " + CqlIdentifier.fromCql(creds.getName().getDataSourceName()));
                  smokCassMap.put(sdsn, cqlSess);
                } else {
                  HikariDataSource hikariDataSource = src.getHikariDataSource();
                  dataSources.put(sdsn,    hikariDataSource);
                }
            }
        }
    }

    public DBCreds getCreds(SmokDatasourceName dbName){
        return envCredMap.getOrDefault(dbName, new DBCreds(new HashMap<>()));
    }

    public HikariDataSource getHikDatasource(SmokDatasourceName dbSrcName){
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
}
