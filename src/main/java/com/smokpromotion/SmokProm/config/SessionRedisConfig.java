package com.smokpromotion.SmokProm.config;

import com.smokpromotion.SmokProm.config.common.YamlDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
@Profile("!memory-session")
public class SessionRedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionRedisConfig.class);

    private static final String HOST_KEY = "SESSION_REDIS_HOST";

    private static final String PORT_KEY = "SESSION_REDIS_PORT";

    private static final String PASSWORD_KEY = "SESSION_REDIS_PASSWORD";

//    @Value("${SESSION_REDIS_HOST:localhost}")
    private String host = "localhost";

//    @Value("${SESSION_REDIS_PASSWORD:}")
    private String password = "";

//    @Value("${SESSION_REDIS_PORT:6379}")
    private String portStr = "6379";

    @Autowired
    private YamlDBConfig yamlDBConfig;
    private int port;

    @Value("${SESSION_REDIS_TIMEOUT:2000}")
    private Integer timeout;


    @Value("${SESSION_REDIS_NODES:NULL}")
    private String redisNodes;

//    @Autowired(required=false)
//    private ClusterConfigurationProperties clusterConfig;

    @Bean
    public JedisConnectionFactory connectionFactory() {

        Map<String, String> props = yamlDBConfig.getAllProps();

        host = props.getOrDefault(HOST_KEY, host);

        portStr = props.getOrDefault(PORT_KEY, portStr);

        password = props.getOrDefault(PASSWORD_KEY, password);

        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e){
            LOGGER.warn("Unparseable port number "+portStr);
        }

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();

        if (redisNodes.equalsIgnoreCase("NULL")) {
            RedisStandaloneConfiguration rsc = new RedisStandaloneConfiguration(host, port);
            jedisConnectionFactory = new JedisConnectionFactory(rsc);
        } else {

  //      if ( (clusterConfig==null || clusterConfig.getNodes()==null ) && redisNodes.equals("NULL")){
  //          LOGGER.info("Using single node redis "+host+":"+port);
  //          jedisConnectionFactory = new JedisConnectionFactory();
  //          jedisConnectionFactory.setHostName(host);
  //          jedisConnectionFactory.setPort(port);
        //} else
        //if (redisNodes.equals("NULL")) {
        //    LOGGER.info("Using Redis cluster specified in from application_s.yml");
        //    jedisConnectionFactory = new JedisConnectionFactory(new RedisClusterConfiguration(clusterConfig.getNodes()));
       // } else {
            LOGGER.info("Using Redis cluster nodes: "+redisNodes);
            String nodes[] = redisNodes.split(",");
            jedisConnectionFactory = new JedisConnectionFactory(new RedisClusterConfiguration(Arrays.stream(nodes).collect(Collectors.toList())));
        }
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setTimeout(timeout);
        return jedisConnectionFactory;
    }

    // This is required to avoid the following exception when running against AWS Elasticache Redis.
    // redis.clients.jedis.exceptions.JedisDataException: ERR unknown command 'CONFIG'
    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

}