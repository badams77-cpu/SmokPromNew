package com.smokpromotion.SmokProm.config.DBs;

import org.springframework.boot.jdbc.DataSourceBuilder;

import java.util.Map;

public class DBCreds {

    private static final String ENV_VAR_NAME = "Name";
    private static final String ENV_VAR_ISOLATION_LEVEL = "IsolationLevel";
    private static final String ENV_VAR_DB_VARIANT = "DbVariant";
    private static final String ENV_VAR_HOST_ADDRESS = "HostAddress";
    private static final String ENV_VAR_PORT = "Port";
    private static final String ENV_VAR_USERNAME = "Username";
    private static final String ENV_VAR_PASSWD = "Password";
    private static final String ENV_VAR_REMOVE_DATABASE_NAME_AT_SERVICE = "removeDatabaseNameAtService";
    private static final String ENV_VAR_PRIORITY = "Priority";
    private static final String ENV_VAR_GROUP = "Group";
    private static final String ENV_VAR_USE_SSL = "UseSSL";
    private static final String ENV_VAR_VERIFY_SSL_CERT = "VerifySSL";

    private static final String[] ENV_VARS = {
                ENV_VAR_NAME,
                ENV_VAR_ISOLATION_LEVEL,
                ENV_VAR_DB_VARIANT,
                ENV_VAR_HOST_ADDRESS,
                ENV_VAR_PORT,
                ENV_VAR_PASSWD,
                ENV_VAR_REMOVE_DATABASE_NAME_AT_SERVICE,
                ENV_VAR_PRIORITY,
                ENV_VAR_GROUP,
                ENV_VAR_USE_SSL,
                ENV_VAR_VERIFY_SSL_CERT
    };


    private final SmokDatasourceName name;
    private final DatabaseVariant variant;
    private final String hostAddress;
    private final int port;
    private final String username;
    private final String passwd;
    private final String removeDatabaseNameAtService;
    private final int priority;
    private final String group;
    private final boolean useSSL;
    private final boolean verifySSLCert;
    private final String isolationLevel;

    public DBCreds(){
        this.name = new SmokDatasourceName("");
        this.variant = DatabaseVariant.NONE;
        this.hostAddress = "127.0.0.1";
        this.port = 3303;
        this.priority =0;
        this.removeDatabaseNameAtService = "";
        this.username = "";
        this.passwd ="";
        this.group = "";
        this.isolationLevel = "";
        this.useSSL = false;
        this.verifySSLCert = false;
    }

    public DBCreds(SmokDatasourceName name, String
            group, int  priorty, DatabaseVariant variant, String remoteDatabaseNameAtService,
                   String hostAddress, int port, String username, String passwd, boolean useSSL, boolean verifySSLCert,
            String isolationLevel)
    {
        this.isolationLevel = isolationLevel;
        this.name = name;
        this.variant = variant;
        this.hostAddress = hostAddress;
        this.port = port;
        this.priority =priorty;
        this.removeDatabaseNameAtService = remoteDatabaseNameAtService;
        this.username = username;
        this.passwd = passwd;
        this.group = group;
        this.useSSL = useSSL;
        this.verifySSLCert = verifySSLCert;
    }

    DBCreds(Map<String, String> cred)
    {
        this.isolationLevel = cred.getOrDefault(ENV_VAR_ISOLATION_LEVEL,"");
        this.name = new SmokDatasourceName(cred.getOrDefault(ENV_VAR_DB_VARIANT,""));
        this.variant = DatabaseVariant.getFromDescription(cred.getOrDefault(ENV_VAR_DB_VARIANT,""));
        this.hostAddress = cred.getOrDefault(ENV_VAR_HOST_ADDRESS,"");
        this.port = Integer.parseInt(cred.getOrDefault(ENV_VAR_PORT, ""));
        this.priority = Integer.parseInt(cred.getOrDefault(ENV_VAR_PRIORITY,""));
        this.removeDatabaseNameAtService = cred.getOrDefault(ENV_VAR_REMOVE_DATABASE_NAME_AT_SERVICE,"" );
        this.username = cred.getOrDefault(ENV_VAR_USERNAME,"");
        this.passwd = cred.getOrDefault(ENV_VAR_GROUP,"");
        this.group = cred.getOrDefault(ENV_VAR_GROUP,"");
        this.useSSL = Boolean.getBoolean(cred.getOrDefault(ENV_VAR_VERIFY_SSL_CERT, ""));
        this.verifySSLCert = Boolean.getBoolean(cred.getOrDefault(ENV_VAR_VERIFY_SSL_CERT, ""));
    }

    public static String[] getCredFields(){
        return ENV_VARS;
    }

    public DatabaseVariant getVariant() {
        return variant;
    }

    public String getIsolationLevel() {
        return isolationLevel;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }

    public boolean isVerifySSLCert() {
        return verifySSLCert;
    }

    public String getGroup() {
        return group;
    }

    public SmokDatasourceName getName() {
        return name;
    }

    public String getRemoveDatabaseNameAtService() {
        return removeDatabaseNameAtService;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isUseSSL() {
        return useSSL;
    }


    @Override
    public String toString() {
        return "DBCreds{" +
                "name=" + name +
                ", variant=" + variant +
                ", hostAddress='" + hostAddress + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", priority=" + priority +
                ", group='" + group + '\'' +
                '}';
    }
}
