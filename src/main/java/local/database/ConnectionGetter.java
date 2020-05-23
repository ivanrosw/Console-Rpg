package local.database;

import local.database.exceptions.DatabaseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionGetter {

    private static final String DB_URL_PROP_KEY = "url";

    public static Connection getConnection() {
        File dbProp = new File(ConnectionGetter.class.getClassLoader().getResource("database.properties").getFile());

        try {
            Properties properties = new Properties();
            properties.load(new FileReader(dbProp));

            return DriverManager.getConnection(properties.getProperty(DB_URL_PROP_KEY), properties);

        } catch (FileNotFoundException e) {
            throw new DatabaseException(dbProp.getName() + " file not found", e);
        } catch (IOException e) {
            throw new DatabaseException("Internal error", e);
        } catch (SQLException e) {
            throw new DatabaseException("Cant connect to database", e);
        }
    }
}
