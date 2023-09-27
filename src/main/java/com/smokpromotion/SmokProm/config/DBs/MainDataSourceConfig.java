package com.smokpromotion.SmokProm.config.DBs;

import com.smokpromotion.SmokProm.domain.repository.MajoranaDBConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Configuration
public class MainDataSourceConfig {

    @Autowired
    private DBEnvSetup dbEbv;

    @Autowired
    private MajoranaDBConnectionFactory dbConnectionFactory;
    @Bean
    public CassandraTemplate getDatasource(){
        SmokDatasourceName main = dbEbv.getMainDBName();
        return dbConnectionFactory.getCassandraTemplate(main).orElseThrow(
                ()-> new RuntimeException("Main DB "+main+" not found"));
    }


}
