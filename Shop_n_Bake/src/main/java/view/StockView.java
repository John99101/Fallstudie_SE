package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.Database;  // Updated import

public class StockView {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public StockView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Stock Management");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(800, 500);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("Stock Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Create table with custom styling
        String[] columns = {"Cake ID", "Name", "Description", "Price", "Stock", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return Boolean.class;
                return super.getColumnClass(column);
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(232, 242, 254));
        
        // Add table to scroll pane with padding
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button panel with modern styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveChanges());
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        loadStock();
    }

    private void loadStock() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT cake_id, name, description, price, stock, stock_availability FROM cakes";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                tableModel.setRowCount(0);
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        String.format("$%.2f", rs.getDouble("price")),
                        rs.getInt("stock"),
                        rs.getBoolean("stock_availability")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading stock: " + e.getMessage());
        }
    }

    private void saveChanges() {
        try (Connection conn = Database.getConnection()) {
            String updateQuery = "UPDATE cakes SET stock = ?, stock_availability = ? WHERE cake_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int cakeId = (Integer) tableModel.getValueAt(i, 0);
                    int stock = Integer.parseInt(tableModel.getValueAt(i, 4).toString());
                    boolean available = (Boolean) tableModel.getValueAt(i, 5);

                    pstmt.setInt(1, stock);
                    pstmt.setBoolean(2, available);
                    pstmt.setInt(3, cakeId);
                    pstmt.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(frame, "Stock updated successfully!");
            loadStock(); // Reload the table
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving changes: " + e.getMessage());
        }
    }

    public void display() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}