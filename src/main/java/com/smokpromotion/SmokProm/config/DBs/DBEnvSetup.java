package com.smokpromotion.SmokProm.config.DBs;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DBEnvSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmokDataSource.class);

    private final static String[] CredFields = DBCreds.getCredFields();

    private static final String PREFIX = "smokDB";

    @Autowired private Environment env;

    private List<DBCreds> envCredList;

    private Map<SmokDatasourceName, DBCreds> envCredMap;

    private Map<SmokDatasourceName, HikariDataSource> dataSources;

    private Map<SmokDatasourceName, SmokDataSource> smokDataSourceMap;

    private Map<SmokDatasourceName, CqlSession> smokCassMap;

    @Autowired
    public DBEnvSetup(Environment env){
        this.env = env;
        envCredMap = new HashMap<>();
        dataSources = new HashMap<>();
        smokDataSourceMap = new HashMap<>();
        smokCassMap = new HashMap<>();
        envCredList = new LinkedList<>();

        Map<String, Object> map = new HashMap();
        for(Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                map.putAll(((MapPropertySource) propertySource).getSource());
            }
        }

        Map<String, Object> dbEnvMap = map.entrySet().stream().filter( en->en.getKey().startsWith(PREFIX+".")).collect(Collectors.toMap(en->en.getKey().substring("smokDb.".length()),en->en.getValue()));

        Set<String> names =  dbEnvMap.keySet().stream().map(x->x.substring(0,x.indexOf("_"))).collect(Collectors.toSet());

        for(String smkDS : names){
            SmokDatasourceName sdsn = new SmokDatasourceName(smkDS);
            Map<String, Object> credData =
                    dbEnvMap.entrySet().stream().filter( en->en.getKey().startsWith(sdsn.getDataSourceName()))
                            .collect(Collectors.toMap( x->x.getKey(), y->y.getValue()));
            int found = 0;
            Map<String, String> credMap = new HashMap<>();

            String stem = PREFIX+"."+smkDS+".";

            for(String field : CredFields) {
                String key = stem + field;
                String value = (String) dbEnvMap.get(key);
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
}
