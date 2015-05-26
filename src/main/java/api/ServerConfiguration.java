package api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: lcignoni
 * Date: 26/05/2015
 * Time: 09:49
 */
public class ServerConfiguration {
    private int port;

    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    ServerConfiguration() throws IOException {
        InputStream confStream = ServerConfiguration.class.getClassLoader().getResourceAsStream("conf.properties");
        Properties props = new Properties();
        props.load(confStream);

        port = Integer.valueOf(props.getProperty("server.port"));

        databaseUrl = props.getProperty("database.url");
        databaseUser = props.getProperty("database.user");
        databasePassword = props.getProperty("database.password");
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }
}
