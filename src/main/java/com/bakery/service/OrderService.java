package src.main.java.com.bakery.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private Connection connection;

    public OrderService(Connection connection) {
        this.connection = connection;
    }

    public boolean createOrder(int userId, String deliveryMethod, List<OrderDetail> orderDetails) {
        String orderQuery = "INSERT INTO Orders (user_id, delivery_method, status) VALUES (?, ?, 'pending')";
        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, userId);
            orderStmt.setString(2, deliveryMethod);
            int rowsAffected = orderStmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    for (OrderDetail detail : orderDetails) {
                        String detailQuery = "INSERT INTO OrderDetails (order_id, product_id, quantity) VALUES (?, ?, ?)";
                        try (PreparedStatement detailStmt = connection.prepareStatement(detailQuery)) {
                            detailStmt.setInt(1, orderId);
                            detailStmt.setInt(2, detail.getProductId());
                            detailStmt.setInt(3, detail.getQuantity());
                            detailStmt.executeUpdate();
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
