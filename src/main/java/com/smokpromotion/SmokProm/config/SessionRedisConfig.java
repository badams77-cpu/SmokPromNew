package com.smokpromotion.SmokProm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Arrays;
import java.util.stream.Collectors;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
@Configuration
@Profile("!memory-session")
public class SessionRedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionRedisConfig.class);

    @Value("${SESSION_REDIS_HOST:localhost}")
    private String host;

    @Value("${SESSION_REDIS_PASSWORD:}")
    private String password;

    @Value("${SESSION_REDIS_PORT:6379}")
    private Integer port;

    @Value("${SESSION_REDIS_TIMEOUT:2000}")
    private Integer timeout;

    @Value("${SESSION_REDIS_NODES:NULL}")
    private String redisNodes;

    @Autowired(required=false)
    private ClusterConfigurationProperties clusterConfig;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory jedisConnectionFactory;
        if ( (clusterConfig==null || clusterConfig.getNodes()==null ) && redisNodes.equals("NULL")){
            LOGGER.info("Using single node redis "+host+":"+port);
            jedisConnectionFactory = new JedisConnectionFactory();
            jedisConnectionFactory.setHostName(host);
            jedisConnectionFactory.setPort(port);
        } else if (redisNodes.equals("NULL")) {
            LOGGER.info("Using Redis cluster specified in from application.yml");
            jedisConnectionFactory = new JedisConnectionFactory(new RedisClusterConfiguration(clusterConfig.getNodes()));
        } else {
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