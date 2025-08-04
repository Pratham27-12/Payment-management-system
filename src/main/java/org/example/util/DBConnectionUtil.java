package org.example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnectionUtil {

    private static final String PROPERTIES_FILE = "applications.properties";
    private static HikariDataSource dataSource;

    static {
        initializeDataSource();
    }

    private static void initializeDataSource() {
        Properties properties = loadProperties();
        if (properties != null) {
            setupHikariDataSource(properties);
        } else {
            throw new RuntimeException("Failed to initialize database connection pool");
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = DBConnectionUtil.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new IOException("Properties file not found: " + PROPERTIES_FILE);
            }

            properties.load(inputStream);
            Class.forName(properties.getProperty("db.driver"));
            return properties;

        } catch (IOException e) {
            System.err.println("Failed to load properties file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load database driver: " + e.getMessage());
        }
        return null;
    }

    private static void setupHikariDataSource(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection returned to pool!");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("HikariCP connection pool shut down.");
        }
    }
}