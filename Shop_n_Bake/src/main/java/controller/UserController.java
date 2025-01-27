package controller;

import util.Database;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getBoolean("is_company")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getBoolean("is_company")
                );
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

    public static User getUserByEmail(String email) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getBoolean("is_company")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM users WHERE role = 'customer'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getBoolean("is_company")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static User promoteToEmployee(User user) {
        if (updateUserRole(user.getId(), "employee")) {
            // Neues User-Objekt mit aktualisierter Rolle zurückgeben
            return new User(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                "employee",
                user.isCompany()
            );
        }
        return user;
    }

    public static User demoteToCustomer(User user) {
        if (updateUserRole(user.getId(), "customer")) {
            // Neues User-Objekt mit aktualisierter Rolle zurückgeben
            return new User(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                "customer",
                user.isCompany()
            );
        }
        return user;
    }

    public static User getUserByEmailAndPassword(String email, String password) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getBoolean("is_company")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean updateUserRole(int userId, String newRole) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE users SET role = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newRole);
                pstmt.setInt(2, userId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}