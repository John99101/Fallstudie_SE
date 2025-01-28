package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import model.Cake;
import model.User;
import model.OrderItem;
import util.Database;
import util.UIManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.stream.IntStream;
import java.util.Arrays;
import javax.swing.ButtonModel;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class CheckoutView {
    private JFrame frame;
    private final User user;
    private final Map<Cake, Integer> cartItems;
    private JTextField addressField;
    private JComboBox<String> deliveryTypeCombo;
    private JSpinner deliveryTimeSpinner;
    private final String[] DELIVERY_OPTIONS = {"Pickup", "Delivery"};
    private BigDecimal totalAmount;

    public CheckoutView(User user, Map<Cake, Integer> cartItems, String address) {
        this.user = user;
        this.cartItems = cartItems;
        this.totalAmount = calculateTotal();
        initialize(address);
    }

    private void initialize(String address) {
        frame = new JFrame("Checkout");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Order summary
        mainPanel.add(createSummaryPanel(), BorderLayout.NORTH);
        
        // Delivery details
        mainPanel.add(createDeliveryPanel(address), BorderLayout.CENTER);
        
        // Total and checkout button
        mainPanel.add(createCheckoutPanel(), BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Map.Entry<Cake, Integer> entry : cartItems.entrySet()) {
            Cake cake = entry.getKey();
            int quantity = entry.getValue();
            listModel.addElement(String.format("%s x%d - $%.2f", 
                cake.getName(), quantity, 
                cake.getPrice().multiply(BigDecimal.valueOf(quantity))));
        }

        JList<String> itemsList = new JList<>(listModel);
        panel.add(new JScrollPane(itemsList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDeliveryPanel(String address) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delivery Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Delivery Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Delivery Type:"), gbc);
        
        gbc.gridx = 1;
        deliveryTypeCombo = new JComboBox<>(DELIVERY_OPTIONS);
        panel.add(deliveryTypeCombo, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        addressField = new JTextField(address, 20);
        panel.add(addressField, gbc);

        // Delivery Time
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Preferred Time:"), gbc);
        
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        deliveryTimeSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(deliveryTimeSpinner, "yyyy-MM-dd HH:mm");
        deliveryTimeSpinner.setEditor(timeEditor);
        panel.add(deliveryTimeSpinner, gbc);

        return panel;
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", totalAmount));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(totalLabel);

        JButton checkoutButton = new JButton("Place Order");
        checkoutButton.addActionListener(e -> placeOrder());
        panel.add(checkoutButton);

        return panel;
    }

    private void placeOrder() {
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Create order
                String orderQuery = "INSERT INTO orders (user_id, created_at, status, delivery_type, " +
                                  "delivery_time, address, total_price) VALUES (?, NOW(), ?, ?, ?, ?, ?)";
                int orderId;
                try (PreparedStatement pstmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, user.getId());
                    pstmt.setString(2, "Processing");
                    pstmt.setString(3, (String) deliveryTypeCombo.getSelectedItem());
                    pstmt.setTimestamp(4, new Timestamp(((java.util.Date) deliveryTimeSpinner.getValue()).getTime()));
                    pstmt.setString(5, addressField.getText().trim());
                    pstmt.setBigDecimal(6, totalAmount);
                    
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                }

                // Create order items and update stock
                String itemQuery = "INSERT INTO order_details (order_id, cake_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";
                String stockQuery = "UPDATE stock SET quantity = quantity - ? WHERE cake_id = ?";
                
                try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                     PreparedStatement stockStmt = conn.prepareStatement(stockQuery)) {
                    
                    for (Map.Entry<Cake, Integer> entry : cartItems.entrySet()) {
                        Cake cake = entry.getKey();
                        int quantity = entry.getValue();

                        // Add order item
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, cake.getCakeId());
                        itemStmt.setInt(3, quantity);
                        itemStmt.setBigDecimal(4, cake.getPrice());
                        itemStmt.executeUpdate();

                        // Update stock
                        stockStmt.setInt(1, quantity);
                        stockStmt.setInt(2, cake.getCakeId());
                        stockStmt.executeUpdate();
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(frame, "Order placed successfully!");
                frame.dispose();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error placing order: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Cart is empty!");
            return false;
        }

        if (deliveryTypeCombo.getSelectedItem().equals("Delivery") && 
            addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter delivery address!");
            return false;
        }

        return true;
    }

    private BigDecimal calculateTotal() {
        return cartItems.entrySet().stream()
            .map(entry -> entry.getKey().getPrice()
                .multiply(BigDecimal.valueOf(entry.getValue())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}