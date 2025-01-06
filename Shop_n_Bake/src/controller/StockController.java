package controller;

import model.Database;

import java.sql.*;

public class StockController {

    // Update stock for a specific ingredient
    public boolean updateStock(String ingredient, int quantityUsed) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE stock SET quantity = quantity - ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantityUsed);
            stmt.setString(2, ingredient);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check low stock levels
    public ResultSet getLowStock() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM stock WHERE quantity <= ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 10); // Example threshold for low stock
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
