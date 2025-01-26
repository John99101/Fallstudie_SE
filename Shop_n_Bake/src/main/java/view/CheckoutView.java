package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.Database;
import controller.OrderController;
import model.Order;
import model.Cake;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDate;
import model.OrderItem;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import util.UIManager;
import model.User;
import java.math.BigDecimal;

public class CheckoutView {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextField firstNameField, lastNameField;
    private JComboBox<String> deliveryTypeBox;
    private JComboBox<String> pickupTimeBox;
    private JPanel addressPanel;
    private JTextField streetField, numberField, zipField;
    private JComboBox<String> stateBox;
    private JComboBox<String> paymentMethodBox;
    private JPanel creditCardPanel;
    private JPanel paymentPanel;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private BigDecimal total;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private OrderController orderController;
    private DatePicker datePicker;
    private final List<OrderItem> items;
    private User user;
    private Map<Cake, Integer> cart;
    private String address;
    private String paymentMethod;

    public CheckoutView(User user, Map<Cake, Integer> cart, String address) {
        this.user = user;
        this.cart = cart;
        this.address = address;
        this.total = calculateTotal(cart);
        this.items = createOrderItems(cart);
        this.orderController = new OrderController();
        initialize();
    }

    private void initialize() {
        frame = new JFrame(UIManager.getText("Checkout"));
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Order Summary
        mainPanel.add(createOrderSummaryPanel(items));

        // Delivery Information
        JPanel deliveryPanel = new JPanel(new GridBagLayout());
        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Delivery Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer Info
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        addFormField(deliveryPanel, "First Name:", firstNameField, gbc, 0);
        addFormField(deliveryPanel, "Last Name:", lastNameField, gbc, 1);

        // Delivery Type
        String[] deliveryTypes = {"Pick-Up", "Delivery"};
        deliveryTypeBox = new JComboBox<>(deliveryTypes);
        addFormField(deliveryPanel, "Delivery Type:", deliveryTypeBox, gbc, 2);

        // Pickup Date and Time
        setupDatePicker();
        
        pickupTimeBox = new JComboBox<>(generatePickupTimes());

        // Add date picker
        gbc.gridx = 0;
        gbc.gridy = 3;
        deliveryPanel.add(new JLabel("Pickup Date:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(datePicker, gbc);

        // Add time picker
        gbc.gridx = 0;
        gbc.gridy = 4;
        deliveryPanel.add(new JLabel("Pickup Time:"), gbc);
        gbc.gridx = 1;
        deliveryPanel.add(pickupTimeBox, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Address Panel
        addressPanel = createAddressPanel();

        // Payment Panel
        paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));
        String[] paymentMethods = {"Credit Card", "PayPal", "Invoice"};
        paymentMethodBox = new JComboBox<>(paymentMethods);
        addFormField(paymentPanel, "Payment Method:", paymentMethodBox, gbc, 0);

        creditCardPanel = createCreditCardPanel();
        creditCardPanel.setVisible(true);

        // Add components to main panel
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(deliveryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(addressPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(paymentPanel);
        mainPanel.add(creditCardPanel);

        // Place Order Button
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(placeOrderButton);

        // Add listeners
        deliveryTypeBox.addActionListener(e -> updateDeliveryType());
        paymentMethodBox.addActionListener(e -> updatePaymentMethod());
        placeOrderButton.addActionListener(e -> handlePlaceOrder());

        // Set up the frame
        frame.add(new JScrollPane(mainPanel));
        frame.setSize(600, 800);
        updateDeliveryType(); // Initial update
    }

    private void setupDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        dateSettings.setAllowKeyboardEditing(false);
        
        // Disable weekends
        dateSettings.setVetoPolicy(date -> 
            date.getDayOfWeek() != DayOfWeek.SATURDAY && 
            date.getDayOfWeek() != DayOfWeek.SUNDAY
        );
        
        // Set date range (today + 14 days, excluding weekends)
        LocalDate today = LocalDate.of(2025, 1, 25);
        LocalDate endDate = today.plusDays(14);
        dateSettings.setDateRangeLimits(today, endDate);
        
        datePicker = new DatePicker(dateSettings);
    }

    private String[] generatePickupTimes() {
        List<String> times = new ArrayList<>();
        // Morning slots (8 AM - 12 PM)
        for (int hour = 8; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(String.format("%02d:%02d", hour, minute));
            }
        }
        // Afternoon slots (1 PM - 8 PM)
        for (int hour = 13; hour < 20; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return times.toArray(new String[0]);
    }

    private JPanel createOrderSummaryPanel(List<OrderItem> items) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        String[] columnNames = {"Cake Name", "Quantity", "Price", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);

        for (OrderItem item : items) {
            BigDecimal itemPrice = item.getCake().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subtotal = itemPrice.multiply(quantity);
            
            tableModel.addRow(new Object[]{
                item.getCake().getName(),
                item.getQuantity(),
                String.format("$%.2f", itemPrice.doubleValue()),
                String.format("$%.2f", subtotal.doubleValue())
            });
        }

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel(String.format("Total: $%.2f", total)));

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateDeliveryType() {
        boolean isPickup = deliveryTypeBox.getSelectedItem().equals("Pick-Up");
        pickupTimeBox.setVisible(isPickup);
        addressPanel.setVisible(!isPickup);
        frame.pack();
    }

    private void updatePaymentMethod() {
        String method = (String) paymentMethodBox.getSelectedItem();
        creditCardPanel.setVisible(method.equals("Credit Card"));
        
        // Show address panel for Invoice or Delivery
        boolean showAddress = method.equals("Invoice") || 
                            deliveryTypeBox.getSelectedItem().equals("Delivery");
        addressPanel.setVisible(showAddress);
        
        frame.pack();
    }

    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delivery Address"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        streetField = new JTextField(20);
        numberField = new JTextField(5);
        zipField = new JTextField(10);
        String[] states = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", 
                          "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", 
                          "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", 
                          "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", 
                          "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        stateBox = new JComboBox<>(states);

        addFormField(panel, "Street:", streetField, gbc, 0);
        addFormField(panel, "Number:", numberField, gbc, 1);
        addFormField(panel, "ZIP Code:", zipField, gbc, 2);
        addFormField(panel, "State:", stateBox, gbc, 3);

        return panel;
    }

    private JPanel createCreditCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Credit Card Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        cardNumberField = new JTextField(19); // Length for 16 digits + 3 spaces
        cardNumberField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String text = cardNumberField.getText();
                int length = text.length();
                
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
                    if (length == 4 || length == 9 || length == 14) {
                        cardNumberField.setText(text + " ");
                    } else if (length >= 19) {
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }
        });

        expiryField = new JTextField(5);
        expiryField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String text = expiryField.getText();
                int length = text.length();
                
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
                    if (length == 2) {
                        expiryField.setText(text + "/");
                    } else if (length >= 5) {
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }
        });

        cvvField = new JTextField(3);
        cvvField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String text = cvvField.getText();
                if (text.length() >= 3 || !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        addFormField(panel, "Card Number:", cardNumberField, gbc, 0);
        addFormField(panel, "Expiry (MM/YY):", expiryField, gbc, 1);
        addFormField(panel, "CVV:", cvvField, gbc, 2);

        return panel;
    }

    private boolean validateExpiryDate(String expiry) {
        try {
            if (!expiry.matches("\\d{2}/\\d{2}")) return false;
            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000;
            
            LocalDate expiryDate = LocalDate.of(year, month, 1);
            return expiryDate.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private void handlePlaceOrder() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter your name");
            return;
        }

        if (deliveryTypeBox.getSelectedItem().equals("Pick-Up")) {
            if (datePicker.getDate() == null) {
                JOptionPane.showMessageDialog(frame, "Please select a pickup date");
                return;
            }
        }

        String paymentMethod = (String) paymentMethodBox.getSelectedItem();
        if (paymentMethod.equals("Credit Card")) {
            if (!validateExpiryDate(expiryField.getText())) {
                JOptionPane.showMessageDialog(frame, "Invalid expiry date");
                return;
            }
            // Add more credit card validation here
        }

        if (deliveryTypeBox.getSelectedItem().equals("Delivery") || 
            paymentMethod.equals("Invoice")) {
            if (streetField.getText().isEmpty() || numberField.getText().isEmpty() || 
                zipField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter complete address");
                return;
            }
        }

        // Process order...
        placeOrder(firstNameField.getText(), lastNameField.getText(), 
                  streetField.getText() + ", " + numberField.getText() + ", " + zipField.getText(), 
                  deliveryTypeBox.getSelectedItem().toString(), 
                  getPickupDateTime());
    }

    private LocalDateTime getPickupDateTime() {
        LocalDate date = datePicker.getDate();
        if (date == null) {
            return null;
        }
        // Convert to LocalDateTime by adding the time component
        return date.atTime(Integer.parseInt(pickupTimeBox.getSelectedItem().toString().split(":")[0]),
                          Integer.parseInt(pickupTimeBox.getSelectedItem().toString().split(":")[1]));
    }

    private void placeOrder(String firstName, String lastName, String address, 
                          String deliveryType, LocalDateTime pickupTime) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert order
                String orderQuery = "INSERT INTO orders (user_id, total, status, delivery_type, payment_method, street, house_number, zip_code, city, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                int orderId;
                try (PreparedStatement pstmt = conn.prepareStatement(orderQuery, 
                                                Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, user.getId());
                    pstmt.setBigDecimal(2, total);
                    pstmt.setString(3, "Pending");
                    pstmt.setString(4, deliveryType);
                    pstmt.setString(5, paymentMethod);
                    pstmt.setString(6, address.split(", ")[0]);
                    pstmt.setString(7, address.split(", ")[1]);
                    pstmt.setString(8, address.split(", ")[2]);
                    pstmt.setString(9, address.split(", ")[3]);
                    pstmt.setString(10, address.split(", ")[4]);
                    pstmt.setTimestamp(11, Timestamp.valueOf(pickupTime));
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to get order ID");
                        }
                    }
                }
                
                // Insert order items
                String itemQuery = "INSERT INTO order_items (order_id, cake_id, quantity) " +
                                 "VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(itemQuery)) {
                    for (OrderItem item : items) {
                        pstmt.setInt(1, orderId);
                        pstmt.setInt(2, item.getCake().getId());
                        pstmt.setInt(3, item.getQuantity());
                        pstmt.executeUpdate();
                    }
                }
                
                conn.commit();
                JOptionPane.showMessageDialog(null, "Order placed successfully!");
                frame.dispose();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error placing order: " + e.getMessage());
        }
    }

    public void display() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private BigDecimal calculateTotal(Map<Cake, Integer> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Cake, Integer> entry : cart.entrySet()) {
            total = total.add(entry.getKey().getPrice()
                      .multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return total;
    }

    private List<OrderItem> createOrderItems(Map<Cake, Integer> cart) {
        List<OrderItem> items = new ArrayList<>();
        for (Map.Entry<Cake, Integer> entry : cart.entrySet()) {
            items.add(new OrderItem(entry.getKey(), entry.getValue()));
        }
        return items;
    }
}