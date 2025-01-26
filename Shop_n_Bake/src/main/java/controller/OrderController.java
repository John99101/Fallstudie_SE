package controller;

import util.Database;
import model.Order;
import model.Cake;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class OrderController {

    // Fetch all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setStatus(rs.getString("status"));
                order.setDeliveryType(rs.getString("delivery_type"));
                order.setAddress(rs.getString("address"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Fetch specific order by ID
    public Order getOrderById(int orderId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setStatus(rs.getString("status"));
                order.setDeliveryType(rs.getString("delivery_type"));
                order.setAddress(rs.getString("address"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));

                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, String newStatus) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a new order
    public boolean createOrder(Order order) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO orders (user_id, status, delivery_type, address, payment_method, total_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setString(2, order.getStatus());
            stmt.setString(3, order.getDeliveryType());
            stmt.setString(4, order.getAddress());
            stmt.setString(5, order.getPaymentMethod());
            stmt.setBigDecimal(6, order.getTotalPrice());

            int rowsAffected = stmt.executeUpdate();

            // If order was successfully created, fetch the generated order ID
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    order.setOrderId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get order details (cakes in an order)
    public List<Cake> getOrderDetails(int orderId) {
        List<Cake> cakes = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT c.cake_id, c.name, c.price, od.quantity " +
                    "FROM order_details od " +
                    "JOIN cakes c ON od.cake_id = c.cake_id " +
                    "WHERE od.order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cake cake = new Cake();
                cake.setCakeId(rs.getInt("cake_id"));
                cake.setName(rs.getString("name"));
                cake.setPrice(rs.getBigDecimal("price"));
                cake.setQuantity(rs.getInt("quantity"));

                cakes.add(cake);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cakes;
    }
}

