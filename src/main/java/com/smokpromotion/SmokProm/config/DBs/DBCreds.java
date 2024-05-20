package com.smokpromotion.SmokProm.config.DBs;

import java.util.Map;

public class DBCreds {

    private static final String ENV_VAR_NAME = "name";

    private static final String ENV_VAR_ENABLED = "enabled";
    private static final String ENV_VAR_ISOLATION_LEVEL = "isolationLevel";
    private static final String ENV_VAR_DB_VARIANT = "dbVariant";
    private static final String ENV_VAR_HOST_ADDRESS = "hostAddress";
    private static final String ENV_VAR_PORT = "port";
    private static final String ENV_VAR_USERNAME = "username";
    private static final String ENV_VAR_PASSWD = "password";
    private static final String ENV_VAR_REMOTE_DATABASE_NAME_AT_SERVICE = "remoteDatabaseNameAtService";
    private static final String ENV_VAR_PRIORITY = "priority";
    private static final String ENV_VAR_GROUP = "group";
    private static final String ENV_VAR_USE_SSL = "useSSL";
    private static final String ENV_VAR_VERIFY_SSL_CERT = "verifySSL";
    private static final String ENV_VAR_ALLOW_PUBLIC_KEY_RETRIEVAL = "allowPublicKeyRetrieval";

    private static final String ENV_VAR_CASS_MASTER = "cassMaster";
    private static final String ENV_VAR_CASS_MASTER_IP_ADDR = "cassMasterIpAddr";
    private static final String ENV_VAR_CASS_MASTER_PORT = "cassMassPort";

    private static final String ENV_VAR_CASS_SLAVE_IP_ADDR = "cassSlaveIpAddr";
    private static final String ENV_VAR_CASS_SLAVE_PORT = "cassSlavePort";

    private static final String[] ENV_VARS = {
                ENV_VAR_NAME,
                ENV_VAR_ENABLED,
                ENV_VAR_ISOLATION_LEVEL,
                ENV_VAR_DB_VARIANT,
                ENV_VAR_HOST_ADDRESS,
                ENV_VAR_PORT,
                ENV_VAR_USERNAME,
                ENV_VAR_PASSWD,
            ENV_VAR_REMOTE_DATABASE_NAME_AT_SERVICE,
                ENV_VAR_PRIORITY,
                ENV_VAR_GROUP,
                ENV_VAR_USE_SSL,
                ENV_VAR_VERIFY_SSL_CERT,
                ENV_VAR_CASS_MASTER,
                ENV_VAR_CASS_MASTER_IP_ADDR,
                ENV_VAR_CASS_MASTER_PORT,
            ENV_VAR_CASS_SLAVE_PORT,
            ENV_VAR_CASS_SLAVE_IP_ADDR
    };


    private final SmokDatasourceName name;
    private final DatabaseVariant variant;
    private final String hostAddress;

    private final boolean enabled;
    private final int port;
    private final String username;
    private final String passwd;
    private final String remoteDatabaseNameAtService;
    private final int priority;
    private final String group;
    private final boolean useSSL;
    private final boolean verifySSLCert;
    private final String isolationLevel;
    private final boolean allowPublicKeyRetrieval;
    private final boolean cassMaster;


    public DBCreds(){
        this.name = new SmokDatasourceName("");
        this.variant = DatabaseVariant.NONE;
        this.enabled = false;
        this.hostAddress = "127.0.0.1";
        this.port = 3303;
        this.priority =0;
        this.remoteDatabaseNameAtService = "";
        this.username = "";
        this.passwd ="";
        this.group = "";
        this.isolationLevel = "";
        this.useSSL = false;
        this.verifySSLCert = false;
        this.allowPublicKeyRetrieval = true;
        this.cassMaster = false;
    }
    public DBCreds(SmokDatasourceName name, String
            group, int  priority, DatabaseVariant variant, String remoteDatabaseNameAtService, boolean enabled,
                   String hostAddress, int port, String username, String passwd, boolean useSSL, boolean verifySSLCert,
            String isolationLevel, boolean allowPublicKeyRetrieval, boolean cassMaster)
    {
        this.isolationLevel = isolationLevel;
        this.name = name;
        this.enabled = enabled;
        this.variant = variant;
        this.hostAddress = hostAddress;
        this.port = port;
        this.priority =priority;
        this.remoteDatabaseNameAtService = remoteDatabaseNameAtService;
        this.username = username;
        this.passwd = passwd;
        this.group = group;
        this.useSSL = useSSL;
        this.verifySSLCert = verifySSLCert;
        this.allowPublicKeyRetrieval = allowPublicKeyRetrieval;
        this.cassMaster = cassMaster;
    }

    DBCreds(Map<String, String> cred)
    {
        this.isolationLevel = cred.getOrDefault(ENV_VAR_ISOLATION_LEVEL,"");
        this.name = new SmokDatasourceName(cred.getOrDefault(ENV_VAR_NAME,""));
        this.enabled = Boolean.valueOf(cred.getOrDefault(ENV_VAR_ENABLED, "false"));
        this.variant = DatabaseVariant.getFromDescription(cred.getOrDefault(ENV_VAR_DB_VARIANT,""));
        this.hostAddress = cred.getOrDefault(ENV_VAR_HOST_ADDRESS,"");
       this.port = Integer.parseInt(cred.getOrDefault(ENV_VAR_PORT, "0"));
        this.priority = Integer.parseInt(cred.getOrDefault(ENV_VAR_PRIORITY,"0"));
        this.remoteDatabaseNameAtService = cred.getOrDefault(ENV_VAR_REMOTE_DATABASE_NAME_AT_SERVICE,"" );
        this.username = cred.getOrDefault(ENV_VAR_USERNAME,"");
        this.cassMaster =Boolean.valueOf(cred.getOrDefault(ENV_VAR_CASS_MASTER,""));
        this.group = cred.getOrDefault(ENV_VAR_GROUP,"");      this.useSSL = Boolean.getBoolean(cred.getOrDefault(ENV_VAR_VERIFY_SSL_CERT, "tree"));
        this.verifySSLCert = Boolean.getBoolean(cred.getOrDefault(ENV_VAR_VERIFY_SSL_CERT, "tree"));
        this.allowPublicKeyRetrieval = Boolean.getBoolean(cred.getOrDefault( ENV_VAR_ALLOW_PUBLIC_KEY_RETRIEVAL,"true"));
        this.passwd = cred.getOrDefault(ENV_VAR_PASSWD, "");
    }
    public boolean isAllowPublicKeyRetrieval() {
        return allowPublicKeyRetrieval;
    }

    public static String[] getCredFields(){
        return ENV_VARS;
    }

    public boolean isEnabled() {
        return enabled;
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

    public String getRemoteDatabaseNameAtService() {
        return remoteDatabaseNameAtService;
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
                ", enabled=" + enabled +
                ", hostAddress='" + hostAddress + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", priority=" + priority +
                ", group='" + group + '\'' +
                '}';
    }
}
