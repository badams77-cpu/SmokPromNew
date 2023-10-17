package com.smokpromotion.SmokProm.domain.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.smokpromotion.SmokProm.config.DBs.DBEnvSetup;
import com.smokpromotion.SmokProm.config.DBs.DatabaseVariant;
import com.smokpromotion.SmokProm.config.DBs.SmokDataSource;
import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.domain.entity.TimeResult;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@Component
public class MajoranaDBConnectionFactory {

    private DBEnvSetup dBSourcesFromEnv;

    private CassandraTemplate mockCass;


    public MajoranaDBConnectionFactory(DBEnvSetup dbs) {
              dBSourcesFromEnv = dbs;
        mockCass = mock(CassandraTemplate.class);
    }



    public Optional<JdbcTemplate> getJdbcTemplate(SmokDatasourceName dbSrcName) {
        HikariDataSource source = dBSourcesFromEnv.getHikDatasource(dbSrcName);
        if (source==null){ return Optional.empty(); }
        return Optional.of(new JdbcTemplate(source));
    }

    public DatabaseVariant getVariant(SmokDatasourceName dbName){
        return dBSourcesFromEnv.getCreds(dbName).getVariant();
    }


    public String translate(String sql, DatabaseVariant dbVariant){
            String translatedSQL = sql;
            if (DatabaseVariant.SQL_SERVER == dbVariant) {
                translatedSQL = sql.replaceAll("now\\(\\)", "SYSDATETIME\\(\\)")
                        .replaceAll("length\\(", "LEN\\(")
                        .replaceAll("ifnull\\(", "isnull(")
                        // SQL server uses TOP instead of LIMIT
                        .replaceAll("(?i)\\{limit1_pre\\}", "TOP 1")
                        .replaceAll("(?i)\\{limit1_post\\}", "");
            } else if (DatabaseVariant.MYSQL == dbVariant) {
                translatedSQL = sql
                        // MySQL server uses LIMIT
                        .replaceAll("(?i)\\{limit1_pre\\}", "")
                        .replaceAll("(?i)\\{limit1_post\\}", "LIMIT 1")
                ;
            } else if (DatabaseVariant.CASSANDRA == dbVariant) {
                translatedSQL = sql

                ;
            }
            return translatedSQL;

    }

    public LocalDateTime getDBTime(SmokDatasourceName dbName){
        DatabaseVariant dbVariant = dBSourcesFromEnv.getSmokDatasource(dbName).getVariant();

        switch(dbVariant){
            case MYSQL:
                // use now(3) to get time with microsecond precision
                return getJdbcTemplate(dbName).map( template->template.query("select now(3) as dbtime", new TimeMapper())).orElse(new LinkedList<LocalDateTime>()).stream().findFirst().orElse(null);
            case SQL_SERVER:
                return getJdbcTemplate(dbName).map( template->template.query("select SYSDATETIME() as dbtime", new TimeMapper())).orElse(new LinkedList<LocalDateTime>()).stream().findFirst().orElse(null);
            case CASSANDRA:
                return getCassandraTemplate(dbName).map( template->template.select("select toTimestamp(now() as dbtime)", TimeResult.class))
                        .stream().findFirst().orElse(new LinkedList<>())
                        .stream().findFirst().orElse(new TimeResult()).getDatetime();
            default:
                return null;
        }
    }

    public Optional<CassandraTemplate> getCassandraTemplate(SmokDatasourceName dbName){
        CqlSession cSess = dBSourcesFromEnv.getCqlSession(dbName);
        CassandraTemplate cass = cSess == null ? mockCass: new  CassandraTemplate(cSess);
        return Optional.of(cass);
    }


    public Optional<NamedParameterJdbcTemplate> getNamedParameterJdbcTemplate(SmokDatasourceName dbName) {
        HikariDataSource hds = dBSourcesFromEnv.getHikDatasource(dbName);    ;
        return Optional.of(new NamedParameterJdbcTemplate(hds));
    }


    public class TimeMapper implements RowMapper<LocalDateTime> {
        @Override
            public LocalDateTime mapRow(ResultSet rs, int i) throws SQLException {
                java.sql.Timestamp ts = rs.getTimestamp("dbtime");
                return ts.toLocalDateTime();
            }
        }
}
