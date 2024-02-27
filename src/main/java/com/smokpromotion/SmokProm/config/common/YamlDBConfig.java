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
import java.nio.charset.Charset;

import org.apache.cassandra.concurrent.SEPExecutor;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.ColumnType;
import org.springframework.data.cassandra.core.convert.ColumnTypeResolver;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.projection.EntityProjection;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//@ConstructorBinding
@Component()
@Primary
//@ConfigurationProperties()//(prefix= "smok-db")
public class YamlDBConfig {

    private static final String YAML_FILE = //"resources/" +
            "/application.yml";

    private static final String PREFIX = "smok-db";

    private static final String SEPERATOR = "-";
    private final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(this.getClass());

    private Map<String, Map<String, String>> entries;

    private Map<String, String> rawEntries;
    public YamlDBConfig(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Resource res = new ClassPathResource(YAML_FILE, classLoader);
        EncodedResource eres = new EncodedResource(res,  Charset.defaultCharset());
        YamlPropertySourceFactory fact = new YamlPropertySourceFactory();
        try {
            PropertiesPropertySource props =fact.createPropertySource(YAML_FILE, eres);
            rawEntries = propToMap(props);
            this.entries = mapProps(rawEntries);
        } catch (Exception e){
            LOGGER.warn("Exception populating YamlConfig ",e);
        }

    }

    public Map<String, String> getRawEntries() {
        return rawEntries;
    }

    private Map<String, String> propToMap(PropertiesPropertySource  prop){
        Map<String, String> map = new HashMap<>();
                for (String key : ( prop).getPropertyNames()) {
                    if (key.startsWith(PREFIX)) {
                        map.put(key.replace(PREFIX+".", ""), prop.getProperty(key).toString());
                    }}
        return map;
    }

    private static Map<String, Map<String, String>> mapProps(Map<String, String> entries){
        return entries.entrySet().stream()
                .map ( e ->{

                    List<String> s = propNametoList(e.getKey());
                    return new AbstractMap.SimpleEntry<>(s, e.getValue());
                })

                .filter(x->
                {
                    List<String> s = ((AbstractMap.SimpleEntry<List<String>, String>) x).getKey();
                    return s.size()>1 && s.get(0).equalsIgnoreCase(PREFIX);
                })
                .collect(
                        Collectors.groupingBy(e-> (String) ((AbstractMap.SimpleEntry<List<String>, String>) e).getKey().get(1),
                        Collectors.toMap(f->(String)  ((AbstractMap.SimpleEntry<List<String>, String>) f).getKey().get(2),
                                g-> (String) ((AbstractMap.SimpleEntry<List<String>, String>) g).getValue())));
    }

    private static List<String> propNametoList(String s){
        return Arrays.asList(s.split(SEPERATOR));
    }

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