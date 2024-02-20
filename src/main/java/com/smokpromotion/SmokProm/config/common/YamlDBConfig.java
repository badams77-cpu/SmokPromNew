package com.smokpromotion.SmokProm.config.common;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.ColumnType;
import org.springframework.data.cassandra.core.convert.ColumnTypeResolver;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.projection.EntityProjection;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//@ConstructorBinding
@Component()
@Primary
@ConfigurationProperties(prefix="smok-db")//(prefix= "smok-db")
public class YamlDBConfig {

    private static final String PREFIX = "smok-db";
    private final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(this.getClass());

    private Map<String, Map<String, String>> entries;

    public static String getPrefix(){
        return PREFIX;
    }



    @Override
    public String toString() {
        if (entries != null) {
//            logger.info(entries.toString());
            return entries.toString();
        } else {
            return null;
        }
    }
    public Map<String , Map<String, String>> getEntries() {
        return entries;
    }
    public void setEntries(Map<String , Map<String, String>> entries) {
        this.entries = entries;
    }
}