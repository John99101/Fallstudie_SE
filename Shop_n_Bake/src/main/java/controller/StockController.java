package controller;

import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // Check low stock items
    public ResultSet getLowStock() {
        try {
            Connection conn = Database.getConnection();
            String sql = "SELECT * FROM stock WHERE quantity <= ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 10);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
