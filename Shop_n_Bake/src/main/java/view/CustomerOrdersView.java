package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.Database;

public class CustomerOrdersView {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private final int userId;

    public CustomerOrdersView(int userId) {
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("My Orders");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(1000, 600);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("My Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Create table with custom styling
        String[] columns = {"Order ID", "Date", "Status", "Delivery Type", "Pickup Time/Date", "Items", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(232, 242, 254));
        
        // Add table to scroll pane with padding
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        loadOrders();
    }

    private void loadOrders() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT o.id, o.order_date, o.status, " +
                          "o.delivery_type, o.pickup_time, " +
                          "GROUP_CONCAT(CONCAT(c.name, ' (', oi.quantity, ')') SEPARATOR ', ') as items, " +
                          "SUM(c.price * oi.quantity) as total " +
                          "FROM orders o " +
                          "JOIN order_items oi ON o.id = oi.order_id " +
                          "JOIN cakes c ON oi.cake_id = c.cake_id " +
                          "WHERE o.user_id = ? " +
                          "GROUP BY o.id, o.order_date, o.status, o.delivery_type, o.pickup_time " +
                          "ORDER BY o.order_date DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    tableModel.setRowCount(0);
                    while (rs.next()) {
                        String pickupInfo = rs.getString("pickup_time");
                        if (pickupInfo != null && !pickupInfo.isEmpty()) {
                            pickupInfo = formatPickupDateTime(pickupInfo);
                        }
                        
                        tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getTimestamp("order_date"),
                            rs.getString("status"),
                            rs.getString("delivery_type"),
                            pickupInfo,
                            rs.getString("items"),
                            String.format("$%.2f", rs.getDouble("total"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading orders: " + e.getMessage());
        }
    }

    private String formatPickupDateTime(String pickupInfo) {
        // Add any date/time formatting you want here
        return pickupInfo;
    }

    public void display() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 