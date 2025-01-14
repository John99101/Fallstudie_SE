package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import model.Database;
import controller.OrderController;
import model.Order;
import java.util.List;
import java.text.SimpleDateFormat;

public class OrdersView {
    private JFrame frame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private final String[] STATUS_OPTIONS = {"Processing", "Baking", "Ready for Pickup", "Out for Delivery", "Delivered", "Cancelled"};

    public void display() {
        frame = new JFrame("Orders Management");
        frame.setSize(1200, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create table model with column names
        String[] columnNames = {"Order ID", "Customer Name", "Order Date", "Items", "Total Price", "Delivery Type", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow status column to be editable
            }
        };
        ordersTable = new JTable(tableModel);
        
        // Set up the status column with combobox
        JComboBox<String> statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        ordersTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Order ID
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer Name
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Order Date
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Items
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Delivery Type
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Status
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Actions

        // Load orders
        loadOrders();

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton saveChangesButton = new JButton("Save Changes");
        JButton viewDetailsButton = new JButton("View Details");

        refreshButton.addActionListener(e -> loadOrders());
        saveChangesButton.addActionListener(e -> saveChanges());
        viewDetailsButton.addActionListener(e -> viewOrderDetails());

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(saveChangesButton);
        buttonsPanel.add(viewDetailsButton);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT o.order_id, u.name, o.order_date, o.total_price, " +
                        "o.delivery_type, o.status, o.address " +
                        "FROM orders o " +
                        "JOIN users u ON o.user_id = u.id " +
                        "ORDER BY o.order_id DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String items = getOrderItems(orderId);
                
                // Format the date properly
                Timestamp orderDate = rs.getTimestamp("order_date");
                String formattedDate = orderDate != null ? 
                    dateFormat.format(orderDate) : "N/A";
                
                Object[] row = {
                    orderId,
                    rs.getString("name"),
                    formattedDate,  // Use the formatted date here
                    items,
                    String.format("$%.2f", rs.getDouble("total_price")),
                    rs.getString("delivery_type"),
                    rs.getString("status"),
                    "Update"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error loading orders: " + e.getMessage() + 
                "\nSQL State: " + e.getSQLState());
        }
    }

    private String getOrderItems(int orderId) {
        StringBuilder items = new StringBuilder();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT c.name, od.quantity " +
                        "FROM order_details od " +
                        "JOIN cakes c ON od.cake_id = c.cake_id " +
                        "WHERE od.order_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if (items.length() > 0) items.append(", ");
                items.append(rs.getString("name"))
                     .append(" (x")
                     .append(rs.getInt("quantity"))
                     .append(")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error loading items";
        }
        return items.toString();
    }

    private void saveChanges() {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int orderId = (Integer) tableModel.getValueAt(i, 0);
                String status = (String) tableModel.getValueAt(i, 6);
                
                pstmt.setString(1, status);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(frame, "Changes saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving changes: " + e.getMessage());
        }
    }

    private void viewOrderDetails(int orderId) {
        // TODO: Implement detailed view of the order
        JOptionPane.showMessageDialog(frame, "Order details functionality coming soon!");
    }

    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (Integer) tableModel.getValueAt(selectedRow, 0);
            viewOrderDetails(orderId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an order to view details.");
        }
    }
}