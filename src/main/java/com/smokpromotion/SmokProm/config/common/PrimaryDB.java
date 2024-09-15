package com.smokpromotion.SmokProm.config.common;

import com.majorana.maj_orm.DBs.CassandraState;
import com.majorana.maj_orm.DBs.DBEnvSetup;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;


@Configuration
public class PrimaryDB {

    Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(PrimaryDB.class);

    @Bean
    @Primary
    @Qualifier("dataSource")
    public DataSource getDataSource(){
        DBEnvSetup setup = new DBEnvSetup(new CassandraState(false), new HashMap<>());
        return setup.getHikDatasource(setup.getMainDBName());

    }
/*(
    {

        DbBean bean = new DbBean();
        try {
            bean.connect();
        } catch (Exception e){
            LOGGER.error("Missing datasource ",e);
        }
        return bean.getDatasource();
    }
*/


}
