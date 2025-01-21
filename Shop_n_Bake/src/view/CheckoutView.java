package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import model.Database;
import controller.OrderController;
import model.Order;
import model.Cake;
import java.util.List;
import java.util.Map;

public class CheckoutView {
    private JFrame frame;
    private Map<Integer, Integer> cart;
    private double totalPrice;
    private List<Cake> cakes;
    private int userId;
    private JTextField addressField;
    private JTextField cardNumberField;
    private JTextField cardExpiryField;
    private JTextField cardCVVField;
    private JComboBox<String> deliveryTypeBox;
    private JComboBox<String> paymentMethodBox;
    private JPanel paymentDetailsPanel;
    private OrderController orderController;

    public CheckoutView(Map<Integer, Integer> cart, double totalPrice, List<Cake> cakes, int userId) {
        this.cart = cart;
        this.totalPrice = totalPrice;
        this.cakes = cakes;
        this.userId = userId;
        this.orderController = new OrderController();
    }

    public void display() {
        frame = new JFrame("Checkout");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Order Summary
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(createOrderSummaryPanel(), gbc);

        // Delivery Options
        gbc.gridy = 1;
        mainPanel.add(createDeliveryPanel(), gbc);

        // Payment Options
        gbc.gridy = 2;
        mainPanel.add(createPaymentPanel(), gbc);

        // Place Order Button
        gbc.gridy = 3;
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(e -> validateAndPlaceOrder());
        mainPanel.add(placeOrderButton, gbc);

        frame.add(new JScrollPane(mainPanel));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        String[] columns = {"Cake Name", "Quantity", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Cake cake = cakes.stream()
                .filter(c -> c.getCakeId() == entry.getKey())
                .findFirst()
                .orElse(null);
            if (cake != null) {
                model.addRow(new Object[]{
                    cake.getName(),
                    entry.getValue(),
                    String.format("$%.2f", cake.getPrice()),
                    String.format("$%.2f", cake.getPrice() * entry.getValue())
                });
            }
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", totalPrice));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(totalLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delivery Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        deliveryTypeBox = new JComboBox<>(new String[]{"Pick-Up", "Delivery"});
        addressField = new JTextField(30);
        addressField.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Delivery Type:"), gbc);

        gbc.gridx = 1;
        panel.add(deliveryTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        panel.add(addressField, gbc);

        deliveryTypeBox.addActionListener(e -> 
            addressField.setEnabled(deliveryTypeBox.getSelectedItem().equals("Delivery")));

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payment Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        paymentMethodBox = new JComboBox<>(new String[]{"Credit Card", "PayPal", "Invoice"});
        cardNumberField = new JTextField(16);
        cardExpiryField = new JTextField(5);
        cardCVVField = new JTextField(3);

        paymentDetailsPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Payment Method:"), gbc);

        gbc.gridx = 1;
        panel.add(paymentMethodBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(paymentDetailsPanel, gbc);

        setupPaymentDetailsPanel();

        paymentMethodBox.addActionListener(e -> updatePaymentDetails());

        return panel;
    }

    private void setupPaymentDetailsPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentDetailsPanel.add(new JLabel("Card Number:"), gbc);

        gbc.gridx = 1;
        paymentDetailsPanel.add(cardNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentDetailsPanel.add(new JLabel("Expiry (MM/YY):"), gbc);

        gbc.gridx = 1;
        paymentDetailsPanel.add(cardExpiryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        paymentDetailsPanel.add(new JLabel("CVV:"), gbc);

        gbc.gridx = 1;
        paymentDetailsPanel.add(cardCVVField, gbc);
    }

    private void updatePaymentDetails() {
        boolean isCard = paymentMethodBox.getSelectedItem().equals("Credit Card");
        cardNumberField.setEnabled(isCard);
        cardExpiryField.setEnabled(isCard);
        cardCVVField.setEnabled(isCard);
    }

    private void validateAndPlaceOrder() {
        // Validate delivery information
        if (deliveryTypeBox.getSelectedItem().equals("Delivery") && addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a delivery address!");
            return;
        }

        // Validate payment information
        if (paymentMethodBox.getSelectedItem().equals("Credit Card")) {
            if (!validateCreditCardDetails()) {
                return;
            }
        }

        // Place the order
        try {
            processOrder(
                deliveryTypeBox.getSelectedItem().toString(),
                addressField.getText(),
                paymentMethodBox.getSelectedItem().toString()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error processing order: " + e.getMessage());
        }
    }

    private boolean validateCreditCardDetails() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
        String expiry = cardExpiryField.getText();
        String cvv = cardCVVField.getText();

        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid 16-digit card number!");
            return false;
        }

        if (!expiry.matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(frame, "Please enter expiry date in MM/YY format!");
            return false;
        }

        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid 3-digit CVV!");
            return false;
        }

        return true;
    }

    private void processOrder(String deliveryType, String address, String paymentMethod) throws SQLException {
        double totalPrice = calculateTotal();
        
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO orders (user_id, delivery_type, address, payment_method, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, deliveryType);
                pstmt.setString(3, address);
                pstmt.setString(4, paymentMethod);
                pstmt.setDouble(5, totalPrice);
                pstmt.setString(6, "Processing");
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        insertOrderDetails(orderId, conn);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error processing order: " + e.getMessage());
            throw e;
        }
    }

    private double calculateTotal() {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Cake cake = cakes.stream()
                .filter(c -> c.getCakeId() == entry.getKey())
                .findFirst()
                .orElse(null);
            if (cake != null) {
                total += cake.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    private void insertOrderDetails(int orderId, Connection conn) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, cake_id, quantity) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, entry.getKey());    // cake_id
                pstmt.setInt(3, entry.getValue());   // quantity
                pstmt.executeUpdate();
            }
        }
        
        JOptionPane.showMessageDialog(frame, "Order placed successfully!");
        frame.dispose();
    }
}