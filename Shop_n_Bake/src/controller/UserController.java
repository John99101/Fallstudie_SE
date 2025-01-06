package controller;

import model.Database;
import model.User;

import java.sql.*;

public class UserController {

    /**
     * Registers a user in the database.
     *
     * @param email      The email of the user.
     * @param name       The name of the user.
     * @param password   The password of the user.
     * @param isEmployee Whether the user is an employee.
     * @param key        The employee registration key (if applicable).
     * @return True if registration is successful, false otherwise.
     */
    public boolean registerUser(String email, String name, String password, boolean isEmployee, String key) {
        try (Connection conn = Database.getConnection()) {
            // If registering as an employee, ensure a key is provided
            if (isEmployee && (key == null || key.isEmpty())) {
                return false; // Reject registration if key is missing
            }

            String sql = "INSERT INTO users (email, name, password, is_employee) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, name);
            stmt.setString(3, password); // Encrypt passwords in production!
            stmt.setBoolean(4, isEmployee);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return True if the email is registered, false otherwise.
     */
    public boolean isEmailRegistered(String email) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates a user based on their email and password.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return A User object if authentication is successful, null otherwise.
     */
    public User loginUser(String email, String password) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password); // Passwords should be hashed in production!

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setEmployee(rs.getBoolean("is_employee"));
                return user; // Return the User object on successful login
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    /**
     * Fetches a user's details by their ID.
     *
     * @param userId The ID of the user.
     * @return A User object if found, null otherwise.
     */
    public User getUserById(int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setEmployee(rs.getBoolean("is_employee"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates a user's details in the database.
     *
     * @param user The User object containing updated details.
     * @return True if the update is successful, false otherwise.
     */
    public boolean updateUser(User user) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET email = ?, name = ?, password = ?, is_employee = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isEmployee());
            stmt.setInt(5, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    public boolean deleteUser(int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}