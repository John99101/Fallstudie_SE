package view;

import javax.swing.*;
import java.awt.*;
import controller.OrderController;
import model.Cake;
import java.sql.*;
import util.Database;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import model.OrderItem;
import util.UIManager;
import java.math.BigDecimal;
import model.CartItem;

public class CustomerView {
    private JFrame frame;
    private final int userId;
    private List<CartItem> cartItems;
    private JPanel mainPanel;
    private JPanel cartPanel;
    private JComboBox<String> deliveryType;
    private JTextField addressField;
    private JComboBox<String> paymentMethod;
    private JLabel totalLabel;

    public CustomerView(int userId) {
        this.userId = userId;
        this.cartItems = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame(UIManager.getText("Shopping Cart"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UIManager.BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        mainPanel.add(UIManager.createHeaderPanel(), BorderLayout.NORTH);

        // Products Panel (Left)
        JPanel productsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        productsPanel.setBackground(UIManager.BG_COLOR);
        loadProducts(productsPanel);
        JScrollPane productsScroll = new JScrollPane(productsPanel);
        productsScroll.setPreferredSize(new Dimension(500, 0));

        // Cart Panel (Right)
        cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(UIManager.BG_COLOR);
        
        // Cart Items
        JPanel cartItemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        cartItemsPanel.setBackground(UIManager.BG_COLOR);
        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);

        // Checkout Panel
        JPanel checkoutPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        checkoutPanel.setBackground(UIManager.BG_COLOR);

        deliveryType = new JComboBox<>(new String[]{
            UIManager.getText("Pickup"),
            UIManager.getText("Delivery")
        });
        addressField = new JTextField(20);
        paymentMethod = new JComboBox<>();
        
        deliveryType.addActionListener(e -> updatePaymentMethods());

        checkoutPanel.add(new JLabel(UIManager.getText("Delivery Type")));
        checkoutPanel.add(deliveryType);
        checkoutPanel.add(new JLabel(UIManager.getText("Address")));
        checkoutPanel.add(addressField);
        checkoutPanel.add(new JLabel(UIManager.getText("Payment Method")));
        checkoutPanel.add(paymentMethod);

        totalLabel = new JLabel(UIManager.getText("Total") + ": €0.00");
        JButton checkoutButton = UIManager.createStyledButton("Proceed to Checkout");
        checkoutButton.addActionListener(e -> handleCheckout());

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(UIManager.BG_COLOR);
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(checkoutButton, BorderLayout.EAST);

        cartPanel.add(cartScroll, BorderLayout.CENTER);
        cartPanel.add(checkoutPanel, BorderLayout.NORTH);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            productsScroll,
            cartPanel
        );
        splitPane.setDividerLocation(500);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        updatePaymentMethods();
    }

    private void loadProducts(JPanel panel) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM cakes WHERE available = true ORDER BY name";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    JPanel productPanel = createProductPanel(
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price")
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

    private JPanel createProductPanel(int cakeId, String name, String description, BigDecimal price) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIManager.BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel nameLabel = new JLabel(name);
        JLabel priceLabel = new JLabel("€" + price);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JButton addButton = UIManager.createStyledButton("Add to Cart");

        addButton.addActionListener(e -> {
            int quantity = (Integer) quantitySpinner.getValue();
            addToCart(cakeId, name, price.doubleValue(), quantity);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(UIManager.BG_COLOR);
        rightPanel.add(quantitySpinner);
        rightPanel.add(addButton);

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(priceLabel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void updatePaymentMethods() {
        paymentMethod.removeAllItems();
        if (deliveryType.getSelectedItem().equals(UIManager.getText("Pickup"))) {
            paymentMethod.addItem(UIManager.getText("Cash"));
        }
        paymentMethod.addItem(UIManager.getText("Credit Card"));
        paymentMethod.addItem(UIManager.getText("Invoice"));
        paymentMethod.addItem(UIManager.getText("PayPal"));
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Cart is empty"),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("Lieferung".equals(deliveryType.getSelectedItem()) && addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Please enter delivery address"),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Create order
                String orderQuery = "INSERT INTO orders (user_id, status, delivery_type, address, payment_method, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                int orderId;
                try (PreparedStatement pstmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, "Processing");
                    pstmt.setString(3, deliveryType.getSelectedItem().toString());
                    pstmt.setString(4, addressField.getText());
                    pstmt.setString(5, paymentMethod.getSelectedItem().toString());
                    pstmt.setBigDecimal(6, calculateTotal());
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                }

                // Add order details
                String detailsQuery = "INSERT INTO order_details (order_id, cake_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(detailsQuery)) {
                    for (CartItem item : cartItems) {
                        pstmt.setInt(1, orderId);
                        pstmt.setInt(2, item.getCakeId());
                        pstmt.setInt(3, item.getQuantity());
                        pstmt.executeUpdate();
                    }
                }

                conn.commit();
                cartItems.clear();
                updateCartDisplay();
                JOptionPane.showMessageDialog(frame,
                    UIManager.getText("Order placed successfully!"),
                    UIManager.getText("Success"),
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                UIManager.getText("Error placing order: ") + e.getMessage(),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogout() {
        frame.dispose();
        new LoginView().display();
    }

    private void showMyOrders() {
        if (userId <= 0) {
            JOptionPane.showMessageDialog(frame, "Error: User ID not found");
            return;
        }
        new CustomerOrdersView(userId).display();
    }

    private BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(BigDecimal.valueOf(item.getPrice() * item.getQuantity()));
        }
        return total;
    }

    private void updateCartDisplay() {
        StringBuilder cartText = new StringBuilder("<html>Cart Contents:<br/>");
        for (CartItem item : cartItems) {
            cartText.append(item.getName())
                .append(" x")
                .append(item.getQuantity())
                .append(" - €")
                .append(String.format("%.2f", item.getPrice() * item.getQuantity()))
                .append("<br/>");
        }
        cartText.append("<br/>Total: €").append(String.format("%.2f", calculateTotal())).append("</html>");
        totalLabel.setText(cartText.toString());
    }

    private void addToCart(int cakeId, String name, double price, int quantity) {
        CartItem item = new CartItem(cakeId, name, price, quantity);
        cartItems.add(item);
        updateCartDisplay();
        JOptionPane.showMessageDialog(frame, quantity + "x " + name + " added to cart!");
    }
}
