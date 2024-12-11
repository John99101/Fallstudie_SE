package src.main.java.com.bakery.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private Connection connection;

    public InventoryService(Connection connection) {
        this.connection = connection;
    }

    public void updateInventory(int productId, int quantity) {
        String query = "UPDATE Inventory SET quantity = quantity - ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Inventory> checkLowStock() {
        List<Inventory> lowStockItems = new ArrayList<>();
        String query = "SELECT * FROM Inventory WHERE quantity < threshold";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lowStockItems.add(new Inventory(rs.getInt("inventory_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("threshold")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockItems;
    }
}