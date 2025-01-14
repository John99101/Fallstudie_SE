package model;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/bakery_db";
    private static final String USER = "root";
    private static final String PASSWORD = "IU1234";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}