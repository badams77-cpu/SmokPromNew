package com.smokpromotion.SmokProm.config.DBs;

import com.smokpromotion.SmokProm.domain.repository.MajoranaDBConnectionFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;

@Configuration
public class MainDataSourceConfig {

    @Autowired
    private DBEnvSetup dbEnv;

    private CassandraTemplate mockCass;

    public MainDataSourceConfig(){
        mockCass = mock(CassandraTemplate.class);
    }

    @Autowired
    private MajoranaDBConnectionFactory dbConnectionFactory;
    @Bean
    public CassandraTemplate getCassDatasource(){
        SmokDatasourceName main = dbEnv.getMainCassDBName();
        CassandraTemplate c = dbConnectionFactory.getCassandraTemplate(main).orElse(mockCass);
        return c;
    }

    @Bean
    public DataSource getDatasource(){
        SmokDatasourceName main = dbEnv.getMainSqlDBName();
        HikariDataSource d = dbEnv.getHikDatasource(main);
          return d;
    }

}
