package com.smokpromotion.SmokProm.config.common;


import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import java.nio.charset.Charset;


import org.slf4j.Logger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component()
@Primary
@ConfigurationProperties(prefix= "smok")
public class YamlDBConfig {

    private static final String YAML_FILE = "/application.yml";

    private static final String PREFIX = "smok-db";

    private static final String SEPERATOR = ".";

    private static final String SEPERATOR_REGEX = "\\.";
    private final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(this.getClass());

    private Map<String, Map<String, String>> entries;

    private Map<String, String> rawEntries;

    private Map<String, String> allProps;

    public YamlDBConfig(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Resource res = new ClassPathResource(YAML_FILE, classLoader);
        EncodedResource eres = new EncodedResource(res,  Charset.defaultCharset());
        YamlPropertySourceFactory fact = new YamlPropertySourceFactory();
        try {
            PropertiesPropertySource props =fact.createPropertySource(YAML_FILE, eres);
            rawEntries = propToMap(props);
            allProps = allPropsToMap(props);
            this.entries = mapProps(rawEntries);
        } catch (Exception e){
            LOGGER.warn("Exception populating YamlConfig ",e);
        }

    }

    public Map<String, String> getRawEntries() {
        return rawEntries;
    }

    public Map<String, String> getAllProps() {
        return allProps;
    }

    public void setAllProps(Map<String, String> allProps) {
        this.allProps = allProps;
    }

    private Map<String, String> propToMap(PropertiesPropertySource  prop){
        Map<String, String> map = new HashMap<>();
                for (String key : ( prop).getPropertyNames()) {
                    if (key.startsWith(PREFIX)) {
                      map.put(key, prop.getProperty(key).toString());
                    }
                }
        return map;
    }

    private Map<String, String> allPropsToMap(PropertiesPropertySource  prop){
        Map<String, String> map = new HashMap<>();
        for (String key : ( prop).getPropertyNames()) {
            map.put(key, prop.getProperty(key).toString());
        }
        return map;
    }

    private static Map<String, Map<String, String>> mapProps(Map<String, String> entries){
        Map<String, Map<String, String>> mymap = entries.entrySet().stream()
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
                        Collectors.groupingBy(e-> (String) ((AbstractMap.SimpleEntry<List<String>, String>) e)
                                        .getKey().get(1),
                        Collectors.toMap(f->(String)  ((AbstractMap.SimpleEntry<List<String>, String>) f)
                                        .getKey().get(2),
                                g-> (String) ((AbstractMap.SimpleEntry<List<String>, String>) g).getValue())));
        return mymap;
    }

    private static List<String> propNametoList(String s){
        List<String> a = Arrays.asList(s.split(SEPERATOR_REGEX));
        return a;
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