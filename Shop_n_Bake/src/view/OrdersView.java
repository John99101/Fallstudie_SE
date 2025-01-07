package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import model.Database;

public class OrdersView {

    private JFrame frame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public void display() {
        frame = new JFrame("Orders");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create table model with column names
        String[] columnNames = {"Order ID", "Customer Name", "Status", "Pick-Up/Delivery", "Total Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        ordersTable = new JTable(tableModel);
        
        // Load orders
        loadOrders();

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        JButton viewOrderButton = new JButton("View Order");
        JButton refreshButton = new JButton("Refresh");

        viewOrderButton.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow != -1) {
                String orderId = ordersTable.getValueAt(selectedRow, 0).toString();
                new OrderDetailsView(orderId).display();
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order to view.");
            }
        });

        refreshButton.addActionListener(e -> loadOrders());

        buttonsPanel.add(viewOrderButton);
        buttonsPanel.add(refreshButton);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void loadOrders() {
        // Clear existing table data
        tableModel.setRowCount(0);

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT o.order_id, u.name, o.status, o.delivery_type, o.total_price " +
                        "FROM orders o " +
                        "JOIN users u ON o.user_id = u.id " +
                        "ORDER BY o.order_id DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("name"),
                    rs.getString("status"),
                    rs.getString("delivery_type"),
                    String.format("$%.2f", rs.getDouble("total_price"))
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading orders: " + e.getMessage());
        }
    }
}