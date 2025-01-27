package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import util.Database;

public class ClosedOrdersView {

    private JFrame frame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public ClosedOrdersView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Closed Orders");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(1000, 600);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("Closed Orders History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Create table with custom styling
        String[] columns = {"Order ID", "Date", "Customer", "Delivery Type", "Pickup Time", "Items"};
        tableModel = new DefaultTableModel(columns, 0);
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(25);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        ordersTable.setSelectionBackground(new Color(232, 242, 254));
        
        // Auto-adjust column widths
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Add table to scroll pane with padding
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Showing completed orders only");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        loadClosedOrders();
    }

    public void refreshOrders() {
        tableModel.setRowCount(0);
        loadClosedOrders();
    }

    private void loadClosedOrders() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT o.id, o.order_date, " +
                          "o.first_name, o.last_name, " +
                          "GROUP_CONCAT(CONCAT(c.name, ' (', oi.quantity, ')') SEPARATOR ', ') as items, " +
                          "o.status " +
                          "FROM orders o " +
                          "JOIN order_items oi ON o.id = oi.order_id " +
                          "JOIN cakes c ON oi.cake_id = c.cake_id " +
                          "WHERE o.status = 'Completed' " +
                          "GROUP BY o.id, o.order_date, o.first_name, o.last_name, o.status";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("items"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading closed orders: " + e.getMessage());
        }
    }

    public void display() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}