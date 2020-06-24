package local.consolerpg.database;

import local.consolerpg.database.exceptions.DatabaseException;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionGetter {

    private static final String DB_URL_PROP_KEY = "url";

    private ConnectionGetter() {
    }

    public static Connection getConnection() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream dbStream = classLoader.getResourceAsStream("database.properties");

        try {
            Properties properties = new Properties();
            properties.load(dbStream);

            return DriverManager.getConnection(properties.getProperty(DB_URL_PROP_KEY), properties);

        } catch (FileNotFoundException e) {
            throw new DatabaseException("Database properties file not found", e);
        } catch (IOException e) {
            throw new DatabaseException("Internal error", e);
        } catch (SQLException e) {
            throw new DatabaseException("Cant connect to database", e);
        }
    }
}
