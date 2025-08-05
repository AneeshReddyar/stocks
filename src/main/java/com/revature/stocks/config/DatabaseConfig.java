package com.revature.stocks.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Database Configuration Class
 * Manages database connections and properties
 */
public class DatabaseConfig {
    
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    private static Properties properties;
    private static DatabaseConfig instance;
    
    // Database connection parameters
    private String url;
    private String username;
    private String password;
    private String driver;
    
    private DatabaseConfig() {
        loadProperties();
        initializeConnectionParameters();
    }
    
    /**
     * Singleton pattern to get DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Load properties from application.properties file
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                logger.severe("Unable to find application.properties file");
                throw new RuntimeException("Configuration file not found");
            }
            
            properties.load(input);
            logger.info("Properties loaded successfully");
            
        } catch (IOException e) {
            logger.severe("Error loading properties: " + e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    /**
     * Initialize database connection parameters from properties
     */
    private void initializeConnectionParameters() {
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
        this.driver = properties.getProperty("db.driver");
        
        // Validate required properties
        if (url == null || username == null || password == null || driver == null) {
            logger.severe("Missing required database configuration properties");
            throw new RuntimeException("Incomplete database configuration");
        }
        
        // Load database driver
        try {
            Class.forName(driver);
            logger.info("Database driver loaded successfully: " + driver);
        } catch (ClassNotFoundException e) {
            logger.severe("Database driver not found: " + driver);
            throw new RuntimeException("Database driver not available", e);
        }
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            logger.info("Database connection established successfully");
            return connection;
        } catch (SQLException e) {
            logger.severe("Failed to establish database connection: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            logger.severe("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close database connection safely
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed successfully");
            } catch (SQLException e) {
                logger.warning("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get property value by key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property value with default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    // Getters for connection parameters
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getDriver() {
        return driver;
    }
}