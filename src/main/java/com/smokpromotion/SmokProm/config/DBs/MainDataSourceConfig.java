package com.smokpromotion.SmokProm.config.DBs;

import com.smokpromotion.SmokProm.domain.repository.MajoranaDBConnectionFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Configuration
public class MainDataSourceConfig {

    @Autowired
    private DBEnvSetup dbEnv;

    @Autowired
    private MajoranaDBConnectionFactory dbConnectionFactory;
    @Bean
    public CassandraTemplate getCassDatasource(){
        SmokDatasourceName main = dbEnv.getMainCassDBName();
        return dbConnectionFactory.getCassandraTemplate(main).orElseThrow(
                ()-> new RuntimeException("Main DB "+main+" not found"));
    }

    @Bean
    public HikariDataSource getDatasource(){
        SmokDatasourceName main = dbEnv.getMainSqlDBName();
        return dbEnv.getHikDatasource(main);
    }

}
