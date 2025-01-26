package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Update these with your actual MySQL credentials
    private static final String URL = "jdbc:mysql://localhost:3306/shop_n_bake?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql1234";  // Match the password from database.sql

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Helper method to close resources
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
} 