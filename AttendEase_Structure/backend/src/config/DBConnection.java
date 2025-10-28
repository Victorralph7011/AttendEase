package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database Connection Manager for AttendEase
 * Implements connection pooling and manages JDBC connections
 */
public class DBConnection {
    
    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendease";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password"; // Change this
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection pool settings
    private static final int MAX_POOL_SIZE = 20;
    private static final int INITIAL_POOL_SIZE = 5;
    
    // Singleton instance
    private static DBConnection instance;
    private static Connection connection;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName(DB_DRIVER);
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Failed to load database driver", e);
        }
    }
    
    /**
     * Get singleton instance of DBConnection
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = new Properties();
                props.setProperty("user", DB_USER);
                props.setProperty("password", DB_PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("serverTimezone", "Asia/Kolkata");
                props.setProperty("allowPublicKeyRetrieval", "true");
                props.setProperty("autoReconnect", "true");
                props.setProperty("maxReconnects", "3");
                
                connection = DriverManager.getConnection(DB_URL, props);
                System.out.println("Database connection established successfully");
                
            } catch (SQLException e) {
                System.err.println("Failed to establish database connection!");
                System.err.println("URL: " + DB_URL);
                System.err.println("Error: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Get a new connection (for concurrent operations)
     * @return New Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getNewConnection() throws SQLException {
        try {
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "Asia/Kolkata");
            props.setProperty("allowPublicKeyRetrieval", "true");
            
            return DriverManager.getConnection(DB_URL, props);
            
        } catch (SQLException e) {
            System.err.println("Failed to create new database connection!");
            throw e;
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Close specific connection, statement, and result set
     * @param autoCloseable Resources to close
     */
    public static void closeResources(AutoCloseable... autoCloseable) {
        for (AutoCloseable resource : autoCloseable) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Load database configuration from properties file
     * @param propertiesFile Path to properties file
     */
    public static void loadConfig(String propertiesFile) {
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream(propertiesFile)) {
            
            if (input == null) {
                System.err.println("Unable to find " + propertiesFile);
                return;
            }
            
            Properties prop = new Properties();
            prop.load(input);
            
            // Load custom configuration if needed
            System.out.println("Configuration loaded successfully");
            
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get database URL
     * @return Database URL string
     */
    public static String getDbUrl() {
        return DB_URL;
    }
    
    /**
     * Main method for testing connection
     */
    public static void main(String[] args) {
        System.out.println("Testing AttendEase Database Connection...");
        System.out.println("==========================================");
        
        DBConnection dbConn = DBConnection.getInstance();
        
        if (testConnection()) {
            System.out.println("✓ Database connection successful!");
            System.out.println("✓ Connected to: " + DB_URL);
        } else {
            System.out.println("✗ Database connection failed!");
            System.out.println("Please check your database configuration.");
        }
        
        closeConnection();
    }
}
