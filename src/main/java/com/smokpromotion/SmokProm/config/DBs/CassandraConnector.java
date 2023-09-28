package com.smokpromotion.SmokProm.config.DBs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

public class CassandraConnector {

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(CassandraConnector.class);
    private CqlSession session;

    private String message = "";

    public void connect(DBCreds creds){
        if (creds.getVariant()==DatabaseVariant.CASSANDRA){
           connect(creds.getHostAddress(), creds.getPort(), creds.getGroup());
        } else {
            message = "Not Cassandra";
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void connect(String node, Integer port, String dataCenter) {
        CqlSessionBuilder builder = CqlSession.builder();
        builder.addContactPoint(new InetSocketAddress(node, port));
        builder.withLocalDatacenter(dataCenter);

        session = builder.build();
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }
}