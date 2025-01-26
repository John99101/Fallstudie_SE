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

public class CheckoutView {
    private JFrame frame;
    private final User user;
    private final Map<Cake, Integer> cart;
    private final BigDecimal total;
    private final List<OrderItem> items;
    
    // Initialize fields before using them
    private JTextField streetField = new JTextField(20);
    private JTextField cityField = new JTextField(20);
    private JTextField zipField = new JTextField(10);
    private ButtonGroup deliveryGroup = new ButtonGroup();
    private ButtonGroup paymentGroup = new ButtonGroup();
    private JComboBox<String> timeSlotCombo;
    private JPanel addressPanel;

    public CheckoutView(User user, Map<Cake, Integer> cart, String address) {
        this.user = user;
        this.cart = cart;
        this.total = calculateTotal(cart);
        this.items = createOrderItems(cart);
        timeSlotCombo = new JComboBox<>(generateTimeSlots());
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Checkout");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.add(createOrderSummaryPanel());
        mainPanel.add(createDeliveryPanel());
        mainPanel.add(createPaymentPanel());
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(createButtonPanel(), BorderLayout.SOUTH);
        frame.setSize(500, 700);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = UIManager.createStyledButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            new CustomerDashboard(user).display();
        });
        
        JButton confirmButton = UIManager.createStyledButton("Confirm Order");
        confirmButton.addActionListener(e -> {
            if (validateInputs()) {
                placeOrder();
            }
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(confirmButton);
        return buttonPanel;
    }

    private JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        String[] columns = {"Item", "Quantity", "Price", "Total"};
        Object[][] data = new Object[cart.size()][4];
        int i = 0;
        for (Map.Entry<Cake, Integer> entry : cart.entrySet()) {
            Cake cake = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal itemTotal = cake.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            data[i] = new Object[]{
                cake.getName(),
                quantity,
                String.format("€%.2f", cake.getPrice()),
                String.format("€%.2f", itemTotal)
            };
            i++;
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel(String.format("Total: €%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delivery Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Delivery type
        JPanel deliveryTypePanel = new JPanel();
        deliveryGroup = new ButtonGroup();
        JRadioButton pickupButton = new JRadioButton("Pickup");
        JRadioButton deliveryButton = new JRadioButton("Delivery", true);
        
        pickupButton.addActionListener(e -> updatePaymentOptions(true));
        deliveryButton.addActionListener(e -> updatePaymentOptions(false));
        
        deliveryGroup.add(pickupButton);
        deliveryGroup.add(deliveryButton);
        deliveryTypePanel.add(pickupButton);
        deliveryTypePanel.add(deliveryButton);

        // Time slot selector
        timeSlotCombo = new JComboBox<>(generateTimeSlots());
        timeSlotCombo.setVisible(false);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Delivery Type:"), gbc);
        gbc.gridx = 1;
        panel.add(deliveryTypePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(timeSlotCombo, gbc);

        // Address fields
        addressPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Street:"), gbc);
        gbc.gridx = 1;
        panel.add(streetField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(cityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("ZIP:"), gbc);
        gbc.gridx = 1;
        panel.add(zipField, gbc);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        
        paymentGroup = new ButtonGroup();
        String[] methods = {"PayPal", "Credit Card", "Invoice", "Cash"};
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        for (int i = 0; i < methods.length; i++) {
            JRadioButton button = new JRadioButton(methods[i]);
            paymentGroup.add(button);
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            panel.add(button, gbc);
        }
        
        return panel;
    }

    private String[] generateTimeSlots() {
        ArrayList<String> slots = new ArrayList<>();
        // Morning slots: 8:00 - 12:00
        for (int hour = 8; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                slots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        // Afternoon slots: 13:00 - 20:00
        for (int hour = 13; hour < 20; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                slots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return slots.toArray(new String[0]);
    }

    private void updatePaymentOptions(boolean isPickup) {
        // Show/hide address fields
        addressPanel.setVisible(!isPickup);
        timeSlotCombo.setVisible(isPickup);
        
        // Update available payment methods
        for (java.util.Enumeration<AbstractButton> buttons = paymentGroup.getElements(); buttons.hasMoreElements();) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.getText().equals("Cash")) {
                button.setEnabled(isPickup);
                if (!isPickup && button.isSelected()) {
                    paymentGroup.clearSelection();
                }
            }
        }
        
        frame.revalidate();
        frame.repaint();
    }

    private boolean validateInputs() {
        if (paymentGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(frame, "Please select a payment method");
            return false;
        }

        boolean isPickup = ((JRadioButton)deliveryGroup.getElements().nextElement()).isSelected();
        if (isPickup && timeSlotCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame, "Please select a pickup time");
            return false;
        }
        
        if (!isPickup && (streetField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() ||
            zipField.getText().trim().isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Please fill in all address fields");
            return false;
        }
        
        return true;
    }

    private void placeOrder() {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String orderQuery = "INSERT INTO orders (user_id, total, status, delivery_address) VALUES (?, ?, ?, ?)";
                int orderId;
                
                String address = String.format("%s, %s %s",
                    streetField.getText().trim(),
                    cityField.getText().trim(),
                    zipField.getText().trim());

                try (PreparedStatement pstmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, user.getId());
                    pstmt.setBigDecimal(2, total);
                    pstmt.setString(3, "Pending");
                    pstmt.setString(4, address);
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to get order ID");
                        }
                    }
                }

                String itemQuery = "INSERT INTO order_items (order_id, cake_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(itemQuery)) {
                    for (OrderItem item : items) {
                        pstmt.setInt(1, orderId);
                        pstmt.setInt(2, item.getCake().getId());
                        pstmt.setInt(3, item.getQuantity());
                        pstmt.executeUpdate();
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(frame, "Order placed successfully!");
                frame.dispose();
                new CustomerDashboard(user).display();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error placing order: " + e.getMessage());
        }
    }

    private BigDecimal calculateTotal(Map<Cake, Integer> cart) {
        return cart.entrySet().stream()
            .map(entry -> entry.getKey().getPrice()
                .multiply(BigDecimal.valueOf(entry.getValue())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItems(Map<Cake, Integer> cart) {
        List<OrderItem> items = new ArrayList<>();
        cart.forEach((cake, quantity) -> items.add(new OrderItem(cake, quantity)));
        return items;
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}