package com.smokpromotion.SmokProm.config.common;

import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:application.yaml" )
@ConfigurationProperties(prefix= "smok-db")
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