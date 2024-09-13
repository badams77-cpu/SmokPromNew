package com.smokpromotion.SmokProm.config.common;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PrimaryDB {

    private DbBean bean;

    public PrimaryDB(){
        bean = new DbBean();
        try {
            bean.connect();
        } catch (Exception e){}
    }

    @Bean
    public DataSource getPrimary(){
        return bean.getDatasource();
    }


}
