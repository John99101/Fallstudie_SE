package view;

import util.UIManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import view.components.ButtonRenderer;
import view.components.ButtonEditor;
import java.math.BigDecimal;
import util.Database;
import model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.util.ArrayList;
import model.Cake;

public class EmployeeView {

    private JFrame frame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private final User currentUser;
    private ClosedOrdersView closedOrdersView;
    private JPanel mainPanel;
    private JPanel productsPanel;
    private JPanel ordersPanel;
    private CardLayout cardLayout;
    private ArrayList<Cake> cakes;

    private static final String[] DEMAND_STATUSES = {

        "Normale Nachfrage",
        "Moderate Nachfrage",
        "Hohe Nachfrage",
        "Geringe Nachfrage"
    };

    public EmployeeView(User user) {
        this.currentUser = user;
        ensureDemandStatusColumn();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Employee Dashboard");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add panels
        mainPanel.add(createOrdersPanel(), "ORDERS");
        mainPanel.add(createInventoryPanel(), "INVENTORY");
        if (isManager()) {
            mainPanel.add(createEmployeeManagementPanel(), "EMPLOYEES");
        }

        // Add navigation panel
        frame.add(createNavPanel(), BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton ordersBtn = new JButton("Orders");
        JButton inventoryBtn = new JButton("Inventory");
        JButton employeesBtn = new JButton("Employees");
        JButton logoutBtn = new JButton("Logout");

        ordersBtn.addActionListener(e -> cardLayout.show(mainPanel, "ORDERS"));
        inventoryBtn.addActionListener(e -> cardLayout.show(mainPanel, "INVENTORY"));
        employeesBtn.addActionListener(e -> cardLayout.show(mainPanel, "EMPLOYEES"));
        logoutBtn.addActionListener(e -> logout());

        navPanel.add(ordersBtn);
        navPanel.add(inventoryBtn);
        if (isManager()) {
            navPanel.add(employeesBtn);
        }
        navPanel.add(logoutBtn);

        return navPanel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Add filter options
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] statusOptions = {"All", "Processing", "Ready for Pickup", "Out for Delivery"};
        JComboBox<String> statusFilter = new JComboBox<>(statusOptions);
        filterPanel.add(new JLabel("Filter by status: "));
        filterPanel.add(statusFilter);
        
        JButton viewClosedBtn = new JButton("View Closed Orders");
        viewClosedBtn.addActionListener(e -> new ClosedOrdersView().display());
        filterPanel.add(viewClosedBtn);
        
        panel.add(filterPanel, BorderLayout.NORTH);

        // Add orders table directly
        JTable ordersTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Order ID", "Customer", "Date", "Items", "Total", "Status", "Actions"},
            0
        );
        ordersTable.setModel(tableModel);
        loadOrdersData(tableModel);
        
        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadOrdersData(DefaultTableModel tableModel) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT o.order_id, u.name as customer_name, " +
                          "o.created_at, o.total_price, o.status " +
                          "FROM orders o " +
                          "JOIN users u ON o.user_id = u.id " +
                          "WHERE o.status != 'Completed' " +
                          "ORDER BY o.created_at DESC";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("created_at"),
                        getOrderItems(rs.getInt("order_id")),
                        String.format("$%.2f", rs.getBigDecimal("total_price")),
                        rs.getString("status"),
                        "View Details"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading orders: " + e.getMessage());
        }
    }

    private String getOrderItems(int orderId) {
        StringBuilder items = new StringBuilder();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT c.name, od.quantity " +
                          "FROM order_details od " +
                          "JOIN cakes c ON od.cake_id = c.cake_id " +
                          "WHERE od.order_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    if (items.length() > 0) items.append(", ");
                    items.append(rs.getString("name"))
                         .append(" (x")
                         .append(rs.getInt("quantity"))
                         .append(")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error loading items";
        }
        return items.toString();
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add new cake button
        JButton addCakeBtn = new JButton("Add New Cake");
        addCakeBtn.addActionListener(e -> addNewCake());
        panel.add(addCakeBtn, BorderLayout.NORTH);

        // Create table for cakes
        String[] columns = {"ID", "Name", "Description", "Price", "Stock", "Actions"};
        Object[][] data = loadCakeData();
        JTable cakesTable = new JTable(data, columns);
        panel.add(new JScrollPane(cakesTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmployeeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add new employee button
        JButton addEmployeeBtn = new JButton("Add New Employee");
        addEmployeeBtn.addActionListener(e -> addNewEmployee());
        panel.add(addEmployeeBtn, BorderLayout.NORTH);

        // Create table for employees
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT u.id, u.name, u.email, e.position, e.hire_date " +
                          "FROM users u " +
                          "JOIN employees e ON u.id = e.user_id " +
                          "WHERE u.is_employee = true";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                // Create table model
                String[] columns = {"ID", "Name", "Email", "Position", "Hire Date", "Actions"};
                DefaultListModel<Object[]> model = new DefaultListModel<>();
                
                while (rs.next()) {
                    model.addElement(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("position"),
                        rs.getDate("hire_date")
                    });
                }
                
                JTable employeesTable = new JTable(new DefaultTableModel(columns, 0));
                panel.add(new JScrollPane(employeesTable), BorderLayout.CENTER);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading employees: " + e.getMessage());
        }

        return panel;
    }

    private Object[][] loadCakeData() {
        ArrayList<Object[]> cakesList = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT c.*, s.quantity as stock " +
                          "FROM cakes c " +
                          "LEFT JOIN stock s ON c.cake_id = s.cake_id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    cakesList.add(new Object[]{
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading cakes: " + e.getMessage());
        }
        
        return cakesList.toArray(new Object[0][]);
    }

    private void addNewCake() {
        // TODO: Implement add new cake functionality
        JOptionPane.showMessageDialog(frame, "Add new cake functionality coming soon!");
    }

    private void addNewEmployee() {
        // TODO: Implement add new employee functionality
        JOptionPane.showMessageDialog(frame, "Add new employee functionality coming soon!");
    }

    private boolean isManager() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT position FROM employees WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
                pstmt.setInt(1, currentUser.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return "Manager".equalsIgnoreCase(rs.getString("position"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void logout() {
        frame.dispose();
        new LoginView().display();
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void ensureDemandStatusColumn() {
        try (Connection conn = Database.getConnection()) {
            // Check if column exists
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "cakes", "demand_status");
            
            if (!rs.next()) {
                // Column doesn't exist, create it
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE cakes ADD COLUMN demand_status VARCHAR(50) DEFAULT 'Normale Nachfrage'");
                    stmt.execute("UPDATE cakes SET demand_status = 'Normale Nachfrage' WHERE demand_status IS NULL");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}