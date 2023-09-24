package com.smokpromotion.SmokProm.config.DBs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;

public class CassandraConnector {

    private CqlSession session;

    public void connect(DBCreds creds){
        if (creds.getVariant()==DatabaseVariant.CASSANDRA){
           connect(creds.getHostAddress(), creds.getPort(), creds.getGroup());
        }
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