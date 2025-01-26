package view;

import util.UIManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import view.components.ButtonRenderer;
import view.components.ButtonEditor;
import java.math.BigDecimal;
import util.Database;
import model.User;

public class EmployeeView {

    private JFrame frame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private final User currentUser;
    private ClosedOrdersView closedOrdersView;
    private JPanel mainPanel;
    private JPanel productsPanel;
    private JPanel ordersPanel;

    public EmployeeView(User user) {
        this.currentUser = user;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Employee Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setBackground(new Color(240, 240, 240));  // Light gray background

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header with title and logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        
        // Title
        JLabel titleLabel = new JLabel("Employee Dashboard");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Logout button
        JButton logoutButton = createAppleButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIManager.BG_COLOR);
        tabbedPane.setForeground(UIManager.FG_COLOR);

        // Products Tab
        productsPanel = createProductsPanel();
        tabbedPane.addTab("Produkte verwalten", productsPanel);

        // Stock Tab
        JPanel stockPanel = createStockPanel();
        tabbedPane.addTab("Lagerbestand", stockPanel);

        // Orders Tab
        ordersPanel = createOrdersPanel();
        tabbedPane.addTab("Bestellungsverwaltung", ordersPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.add(mainPanel);
    }

    private JButton createAppleButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        button.setForeground(new Color(50, 50, 50));
        button.setBackground(new Color(255, 255, 255));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255));
            }
        });
        
        return button;
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIManager.BG_COLOR);

        // Add Product Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UIManager.BG_COLOR);
        
        JButton addProductButton = UIManager.createStyledButton("Add New Product");
        addProductButton.addActionListener(e -> showAddProductDialog());
        buttonPanel.add(addProductButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // Products Grid
        JPanel productsGrid = new JPanel(new GridLayout(0, 1, 10, 10));
        productsGrid.setBackground(UIManager.BG_COLOR);
        loadProducts(productsGrid);
        
        JScrollPane scrollPane = new JScrollPane(productsGrid);
        scrollPane.setBackground(UIManager.BG_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadProducts(JPanel panel) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM cakes ORDER BY name";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    JPanel productPanel = createProductPanel(
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("available")
                    );
                    panel.add(productPanel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Error loading products: ") + e.getMessage(),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createProductPanel(int cakeId, String name, String description, 
                                    BigDecimal price, boolean available) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIManager.BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(UIManager.BG_COLOR);
        infoPanel.add(new JLabel(name));
        infoPanel.add(new JLabel("€" + price));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIManager.BG_COLOR);

        JButton editButton = UIManager.createStyledButton("Edit Product");
        editButton.addActionListener(e -> handleProductEdit(cakeId));
        
        JButton deleteButton = UIManager.createStyledButton("Delete Product");
        deleteButton.addActionListener(e -> handleProductDelete(cakeId));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIManager.BG_COLOR);

        // Add refresh button
        JButton refreshButton = UIManager.createStyledButton("Refresh Orders");
        refreshButton.addActionListener(e -> loadOrders((DefaultTableModel) ordersTable.getModel()));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UIManager.BG_COLOR);
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);

        // Orders Table with improved columns
        String[] columnNames = {
            "Order ID",
            "Customer",
            "Products",
            "Total",
            "Status",
            "Delivery Type",
            "Address/Time",
            "Payment Method",
            "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 1;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setBackground(UIManager.BG_COLOR);
        ordersTable.setForeground(UIManager.FG_COLOR);
        
        // Set up the action button column
        ordersTable.getColumnModel().getColumn(columnNames.length - 1)
            .setCellRenderer(new ButtonRenderer());
        ordersTable.getColumnModel().getColumn(columnNames.length - 1)
            .setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Initial load
        loadOrders(tableModel);

        return panel;
    }

    private void loadOrders(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT o.order_id, u.email, o.total, o.status " +
                          "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                          "WHERE o.status != 'closed' ORDER BY o.order_id DESC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String customer = rs.getString("email");
                    double total = rs.getDouble("total");
                    String status = rs.getString("status");
                    
                    JButton completeButton = UIManager.createStyledButton("Complete");
                    completeButton.addActionListener(e -> handleOrderComplete(orderId));
                    
                    model.addRow(new Object[]{
                        orderId,
                        customer,
                        getOrderProducts(orderId),
                        String.format("€%.2f", total),
                        status,
                        completeButton
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getOrderProducts(int orderId) {
        StringBuilder products = new StringBuilder();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT c.name, oi.quantity FROM order_items oi " +
                          "JOIN cakes c ON oi.cake_id = c.cake_id " +
                          "WHERE oi.order_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (products.length() > 0) products.append(", ");
                    products.append(rs.getString("name"))
                           .append(" (")
                           .append(rs.getInt("quantity"))
                           .append(")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products.toString();
    }

    private void handleOrderComplete(int orderId) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE orders SET status = 'closed' WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
                loadOrders((DefaultTableModel) ordersTable.getModel());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Error updating order: ") + e.getMessage(),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(frame, UIManager.getText("Add New Product"), true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBackground(UIManager.BG_COLOR);
        
        JTextField nameField = new JTextField();
        JTextArea descField = new JTextArea(3, 20);
        JTextField priceField = new JTextField();
        JCheckBox availableBox = new JCheckBox("", true);
        
        inputPanel.add(new JLabel(UIManager.getText("Product Name")));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel(UIManager.getText("Description")));
        inputPanel.add(new JScrollPane(descField));
        inputPanel.add(new JLabel(UIManager.getText("Price")));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel(UIManager.getText("Available")));
        inputPanel.add(availableBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIManager.BG_COLOR);
        
        JButton saveButton = UIManager.createStyledButton("Save");
        JButton cancelButton = UIManager.createStyledButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                saveNewProduct(
                    nameField.getText(),
                    descField.getText(),
                    Double.parseDouble(priceField.getText()),
                    availableBox.isSelected()
                );
                dialog.dispose();
                refreshProducts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    UIManager.getText("Error saving product: ") + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void saveNewProduct(String name, String description, double price, boolean available) {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO cakes (name, description, price, available) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setDouble(3, price);
                pstmt.setBoolean(4, available);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLogout() {
        frame.dispose();
        new LoginView().display();
    }

    private void handleProductEdit(int cakeId) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM cakes WHERE cake_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, cakeId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    showEditDialog(cakeId, rs.getString("name"), 
                                 rs.getString("description"),
                                 rs.getBigDecimal("price"),
                                 rs.getBoolean("available"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Error loading product: ") + e.getMessage(),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleProductDelete(int cakeId) {
        int confirm = JOptionPane.showConfirmDialog(frame,
            UIManager.getText("Are you sure you want to delete this product?"),
            UIManager.getText("Confirm Delete"),
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String query = "DELETE FROM cakes WHERE cake_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, cakeId);
                    pstmt.executeUpdate();
                    refreshProducts();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                    UIManager.getText("Error deleting product: ") + e.getMessage(),
                    UIManager.getText("Error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshProducts() {
        JPanel productsGrid = (JPanel) ((JScrollPane) productsPanel.getComponent(1)).getViewport().getView();
        productsGrid.removeAll();
        loadProducts(productsGrid);
        productsGrid.revalidate();
        productsGrid.repaint();
    }

    private void showEditDialog(int cakeId, String name, String description, BigDecimal price, boolean available) {
        JDialog dialog = new JDialog(frame, "Edit Product", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBackground(UIManager.BG_COLOR);
        
        JTextField nameField = new JTextField(name);
        JTextArea descField = new JTextArea(description, 3, 20);
        JTextField priceField = new JTextField(price.toString());
        JCheckBox availableBox = new JCheckBox("", available);
        
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JScrollPane(descField));
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Available:"));
        inputPanel.add(availableBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIManager.BG_COLOR);
        
        JButton saveButton = UIManager.createStyledButton("Save");
        JButton cancelButton = UIManager.createStyledButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                updateProduct(cakeId, 
                    nameField.getText(),
                    descField.getText(),
                    new BigDecimal(priceField.getText()),
                    availableBox.isSelected()
                );
                dialog.dispose();
                refreshProducts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating product: " + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void updateProduct(int cakeId, String name, String description, BigDecimal price, boolean available) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE cakes SET name = ?, description = ?, price = ?, available = ? WHERE cake_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setBigDecimal(3, price);
                pstmt.setBoolean(4, available);
                pstmt.setInt(5, cakeId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIManager.BG_COLOR);
        
        // Create table model for stock
        String[] columnNames = {
            "Produkt",
            "Verfügbar",
            "Verkauft heute",
            "Verkauft gesamt",
            "Status",
            "Aktion"
        };
        
        DefaultTableModel stockModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) return Boolean.class; // For checkbox in "Verfügbar" column
                return super.getColumnClass(column);
            }
        };
        
        JTable stockTable = new JTable(stockModel);
        stockTable.setBackground(UIManager.BG_COLOR);
        stockTable.setForeground(UIManager.FG_COLOR);
        
        // Add refresh button
        JButton refreshButton = UIManager.createStyledButton("Aktualisieren");
        refreshButton.addActionListener(e -> loadStockData(stockModel));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UIManager.BG_COLOR);
        topPanel.add(refreshButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        
        // Initial load
        loadStockData(stockModel);
        
        return panel;
    }

    private void loadStockData(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection()) {
            String query = """
                SELECT c.name, c.available, 
                       COUNT(CASE WHEN o.created_at >= CURRENT_DATE THEN 1 END) as today_sales,
                       COUNT(oi.order_id) as total_sales,
                       c.cake_id
                FROM cakes c
                LEFT JOIN order_items oi ON c.cake_id = oi.cake_id
                LEFT JOIN orders o ON oi.order_id = o.order_id
                GROUP BY c.cake_id, c.name, c.available
                ORDER BY c.name
                """;
                
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    String status = determineStatus(
                        rs.getInt("today_sales"),
                        rs.getBoolean("available")
                    );
                    
                    JButton updateButton = UIManager.createStyledButton("Aktualisieren");
                    final int cakeId = rs.getInt("cake_id");
                    updateButton.addActionListener(e -> showStockUpdateDialog(cakeId));
                    
                    model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getBoolean("available"),
                        rs.getInt("today_sales"),
                        rs.getInt("total_sales"),
                        status,
                        updateButton
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error loading stock data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String determineStatus(int todaySales, boolean available) {
        if (!available) return "Nicht verfügbar";
        if (todaySales > 10) return "Hohe Nachfrage";
        if (todaySales > 5) return "Moderate Nachfrage";
        return "Normale Nachfrage";
    }

    private void showStockUpdateDialog(int cakeId) {
        JDialog dialog = new JDialog(frame, "Lagerbestand aktualisieren", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JCheckBox availableBox = new JCheckBox("Verfügbar");
        
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(availableBox);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = UIManager.createStyledButton("Speichern");
        JButton cancelButton = UIManager.createStyledButton("Abbrechen");
        
        saveButton.addActionListener(e -> {
            updateProductAvailability(cakeId, availableBox.isSelected());
            dialog.dispose();
            loadStockData((DefaultTableModel)((JTable)((JScrollPane)((JPanel)frame
                .getContentPane().getComponent(0)).getComponent(1))
                .getViewport().getView()).getModel());
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void updateProductAvailability(int cakeId, boolean available) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE cakes SET available = ? WHERE cake_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setBoolean(1, available);
                pstmt.setInt(2, cakeId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error updating product availability: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}