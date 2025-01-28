package view;

import model.Cake;
import util.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OrderDetailsView {

    private JFrame frame;
    private final int orderId;

    public OrderDetailsView(int orderId) {
        this.orderId = orderId;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Order Details - Order #" + orderId);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add order details
        mainPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        
        // Add buttons
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT o.*, u.name as customer_name, " +
                          "GROUP_CONCAT(CONCAT(c.name, ' (', od.quantity, ')') SEPARATOR ', ') as items " +
                          "FROM orders o " +
                          "JOIN users u ON o.user_id = u.id " +
                          "JOIN order_details od ON o.order_id = od.order_id " +
                          "JOIN cakes c ON od.cake_id = c.cake_id " +
                          "WHERE o.order_id = ? " +
                          "GROUP BY o.order_id";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addField(panel, gbc, "Order ID:", String.valueOf(orderId));
                    addField(panel, gbc, "Customer:", rs.getString("customer_name"));
                    addField(panel, gbc, "Order Date:", rs.getTimestamp("created_at").toString());
                    addField(panel, gbc, "Status:", rs.getString("status"));
                    addField(panel, gbc, "Delivery Type:", rs.getString("delivery_type"));
                    addField(panel, gbc, "Address:", rs.getString("address"));
                    addField(panel, gbc, "Total Price:", String.format("$%.2f", rs.getBigDecimal("total_price")));
                    addField(panel, gbc, "Items:", rs.getString("items"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading order details: " + e.getMessage());
        }

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
        gbc.gridy++;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        panel.add(closeButton);
        return panel;
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}