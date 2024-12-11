package src.main.java.com.bakery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/your_database";
        String username = "your_username";
        String password = "your_password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(DatabaseInitializer.class.getResourceAsStream("/bakery_database.sql")));      
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            reader.close();

            Statement statement = connection.createStatement();
            statement.execute(sql.toString());
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database initialization failed!");
        }
    }
}
