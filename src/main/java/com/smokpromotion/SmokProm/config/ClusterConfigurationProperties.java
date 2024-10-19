package com.smokpromotion.SmokProm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dxpulse_portal", "dxpulse_admin"})
@Component
//    @ConfigurationProperties(prefix = "session.redis.cluster")
    public class ClusterConfigurationProperties {

        /*
         * session.redis.cluster.nodes[0] = 127.0.0.1:7379
         * session.redis.cluster.nodes[1] = 127.0.0.1:7380
         * ...
         */
        List<String> nodes;

        /**
         * Get initial collection of known cluster nodes in format {@code host:port}.
         *
         * @return
         */
        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }
    }
