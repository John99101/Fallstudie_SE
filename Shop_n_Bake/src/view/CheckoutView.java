package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import model.Cake;
import model.Database;
import controller.OrderController;
import model.Order;

public class CheckoutView {
    private JFrame frame;
    private Map<Integer, Integer> cart;
    private double totalPrice;
    private java.util.List<Cake> cakes;
    private int userId;
    private JPanel paymentDetailsPanel;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;

    public CheckoutView(Map<Integer, Integer> cart, double totalPrice, java.util.List<Cake> cakes, int userId) {
        this.cart = cart;
        this.totalPrice = totalPrice;
        this.cakes = cakes;
        this.userId = userId;
    }

    public void display() {
        frame = new JFrame("Checkout");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Delivery Options
        JPanel deliveryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deliveryPanel.add(new JLabel("Delivery Type:"));
        JComboBox<String> deliveryType = new JComboBox<>(new String[]{"Pick-Up", "Delivery"});
        deliveryPanel.add(deliveryType);
        mainPanel.add(deliveryPanel);

        // Address Panel
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField addressField = new JTextField(30);
        addressPanel.add(new JLabel("Delivery Address:"));
        addressPanel.add(addressField);
        addressPanel.setVisible(false);
        mainPanel.add(addressPanel);

        deliveryType.addActionListener(e -> 
            addressPanel.setVisible(deliveryType.getSelectedItem().equals("Delivery")));

        // Payment Method
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.add(new JLabel("Payment Method:"));
        JComboBox<String> paymentMethod = new JComboBox<>(new String[]{"Credit Card", "PayPal", "Invoice"});
        paymentPanel.add(paymentMethod);
        mainPanel.add(paymentPanel);

        // Payment Details Panel
        paymentDetailsPanel = new JPanel();
        paymentDetailsPanel.setLayout(new BoxLayout(paymentDetailsPanel, BoxLayout.Y_AXIS));
        paymentDetailsPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));

        // Credit Card Fields
        cardNumberField = new JTextField(16);
        expiryField = new JTextField(5);
        cvvField = new JTextField(3);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardPanel.add(new JLabel("Card Number:"));
        cardPanel.add(cardNumberField);
        paymentDetailsPanel.add(cardPanel);

        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expiryPanel.add(new JLabel("Expiry (MM/YY):"));
        expiryPanel.add(expiryField);
        paymentDetailsPanel.add(expiryPanel);

        JPanel cvvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cvvPanel.add(new JLabel("CVV:"));
        cvvPanel.add(cvvField);
        paymentDetailsPanel.add(cvvPanel);

        mainPanel.add(paymentDetailsPanel);

        paymentMethod.addActionListener(e -> {
            boolean isCard = paymentMethod.getSelectedItem().equals("Credit Card");
            paymentDetailsPanel.setVisible(isCard);
        });

        // Order Summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Cake cake = cakes.stream()
                .filter(c -> c.getCakeId() == entry.getKey())
                .findFirst()
                .orElse(null);
            if (cake != null) {
                summaryPanel.add(new JLabel(cake.getName() + " x" + entry.getValue() + 
                    " - $" + String.format("%.2f", cake.getPrice() * entry.getValue())));
            }
        }
        summaryPanel.add(new JLabel("Total: $" + String.format("%.2f", totalPrice)));
        mainPanel.add(summaryPanel);

        // Place Order Button
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(e -> {
            if (validateOrder(paymentMethod.getSelectedItem().toString(), 
                            deliveryType.getSelectedItem().toString(), 
                            addressField.getText())) {
                placeOrder(deliveryType.getSelectedItem().toString(),
                          addressField.getText(),
                          paymentMethod.getSelectedItem().toString());
            }
        });
        mainPanel.add(placeOrderButton);

        frame.add(new JScrollPane(mainPanel));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean validateOrder(String paymentMethod, String deliveryType, String address) {
        if (deliveryType.equals("Delivery") && address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a delivery address!");
            return false;
        }

        if (paymentMethod.equals("Credit Card")) {
            if (cardNumberField.getText().length() != 16) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid 16-digit card number!");
                return false;
            }
            if (!expiryField.getText().matches("\\d{2}/\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "Please enter expiry date in MM/YY format!");
                return false;
            }
            if (cvvField.getText().length() != 3) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid 3-digit CVV!");
                return false;
            }
        }
        return true;
    }

    private void placeOrder(String deliveryType, String address, String paymentMethod) {
        Order order = new Order();
        order.setUserId(userId);
        order.setDeliveryType(deliveryType);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Processing");
        order.setTotalPrice(totalPrice);

        OrderController orderController = new OrderController();
        if (orderController.createOrder(order)) {
            boolean success = true;
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                try (Connection conn = Database.getConnection()) {
                    String sql = "INSERT INTO order_details (order_id, cake_id, quantity) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, order.getOrderId());
                    stmt.setInt(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    success = false;
                    JOptionPane.showMessageDialog(frame, "Error saving order details!");
                    break;
                }
            }
            
            if (success) {
                JOptionPane.showMessageDialog(frame, "Order placed successfully!");
                frame.dispose();
                // Also dispose the customer view to reset the cart
                // You might want to add a reference to the customer view and dispose it here
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to place order. Please try again.");
        }
    }
}