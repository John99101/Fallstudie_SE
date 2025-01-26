package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

import model.User;
import model.Cake;
import util.Database;
import util.UIManager;

public class CustomerDashboard {
    private JFrame frame;
    private User currentUser;
    private JPanel cartPanel;
    private JPanel addressPanel;
    private JLabel totalLabel;
    
    // Adressfelder
    private JTextField streetField;
    private JTextField houseNumberField;
    private JTextField zipCodeField;
    private JTextField cityField;
    private JTextField countryField;
    
    // Warenkorb
    private Map<Cake, Integer> cart;
    
    public CustomerDashboard(User user) {
        this.currentUser = user;
        this.cart = new HashMap<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Shop 'n' Bake - " + UIManager.getText("Customer Dashboard"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Initialize totalLabel if it hasn't been initialized yet
        totalLabel = new JLabel("Total: €0.00");
        
        // Layout
        frame.setLayout(new BorderLayout());
        
        // Header
        UIManager.addHeaderToFrame(frame);
        
        // Hauptbereich mit Produktliste und Warenkorb
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createProductPanel());
        splitPane.setRightComponent(createOrderPanel());
        splitPane.setDividerLocation(500);
        
        frame.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(UIManager.BG_COLOR);

        // Title
        JLabel titleLabel = new JLabel(UIManager.getText("Available Products"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(UIManager.FG_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productPanel.add(titleLabel, BorderLayout.NORTH);

        // Products Grid
        JPanel productsGrid = new JPanel(new GridLayout(0, 2, 10, 10));
        productsGrid.setBackground(UIManager.BG_COLOR);
        
        // Load products from database
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM cakes WHERE available = true";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Cake cake = new Cake(
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_availability")
                    );
                    productsGrid.add(createProductCard(cake));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading products: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(productsGrid);
        scrollPane.setBorder(null);
        productPanel.add(scrollPane, BorderLayout.CENTER);
        
        return productPanel;
    }

    private JPanel createProductCard(Cake cake) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(cake.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel descLabel = new JLabel(cake.getDescription());
        JLabel priceLabel = new JLabel(String.format("€%.2f", cake.getPrice().doubleValue()));
        
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(priceLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Add quantity selector
        JTextField quantityField = new JTextField("1", 3);
        buttonPanel.add(new JLabel("Qty:"));
        buttonPanel.add(quantityField);

        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity > 0) {
                    addToCart(cake, quantity);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number");
            }
        });
        buttonPanel.add(addButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addToCart(Cake cake, int quantity) {
        cart.merge(cake, quantity, Integer::sum);
        updateCartPanel();
    }

    private void updateCartPanel() {
        cartPanel.removeAll();
        cartPanel.setLayout(new BorderLayout());
        
        JPanel itemsPanel = new JPanel(new GridLayout(0, 1));
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<Cake, Integer> entry : cart.entrySet()) {
            Cake cake = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal itemTotal = cake.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(itemTotal);
            
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            itemPanel.add(new JLabel(String.format("%dx %s - €%.2f", 
                quantity, cake.getName(), itemTotal.doubleValue())));
            
            JButton removeButton = UIManager.createStyledButton("-");
            removeButton.addActionListener(e -> removeFromCart(cake));
            itemPanel.add(removeButton);
            
            itemsPanel.add(itemPanel);
        }
        
        cartPanel.add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
        totalLabel.setText(String.format("Total: €%.2f", total.doubleValue()));
        cartPanel.add(totalLabel, BorderLayout.SOUTH);
        
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private void removeFromCart(Cake cake) {
        cart.computeIfPresent(cake, (k, v) -> v > 1 ? v - 1 : null);
        updateCartPanel();
    }

    private JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(UIManager.BG_COLOR);
        
        // Initialize cart panel if it hasn't been initialized yet
        cartPanel = new JPanel();
        cartPanel.setBackground(UIManager.BG_COLOR);
        
        // Warenkorb oben
        updateCartPanel();  // Initial update of cart panel
        
        // Adressfelder in der Mitte
        addressPanel = createAddressPanel();
        
        // Checkout-Button unten
        JButton checkoutButton = UIManager.createStyledButton(UIManager.getText("Checkout"));
        checkoutButton.addActionListener(e -> handleCheckout());
        
        orderPanel.add(cartPanel, BorderLayout.NORTH);
        orderPanel.add(addressPanel, BorderLayout.CENTER);
        orderPanel.add(checkoutButton, BorderLayout.SOUTH);
        
        return orderPanel;
    }

    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBackground(UIManager.BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(UIManager.getText("Delivery Address")));

        panel.add(new JLabel(UIManager.getText("Street:")));
        streetField = new JTextField(20);
        panel.add(streetField);

        panel.add(new JLabel(UIManager.getText("House Number:")));
        houseNumberField = new JTextField(5);
        panel.add(houseNumberField);

        panel.add(new JLabel(UIManager.getText("ZIP Code:")));
        zipCodeField = new JTextField(5);
        panel.add(zipCodeField);

        panel.add(new JLabel(UIManager.getText("City:")));
        cityField = new JTextField(20);
        panel.add(cityField);

        panel.add(new JLabel(UIManager.getText("Country:")));
        countryField = new JTextField(20);
        panel.add(countryField);

        return panel;
    }

    private void handleCheckout() {
        // Validierung der Adressfelder
        if (!validateAddress()) {
            return;
        }
        
        // Wenn alles okay, öffne CheckoutView
        frame.dispose();
        new CheckoutView(currentUser, cart, getFormattedAddress()).display();
    }

    private boolean validateAddress() {
        // Validierung der Adressfelder
        // ... Validierungslogik
        return true;
    }

    private String getFormattedAddress() {
        return String.format("%s %s, %s %s, %s",
            streetField.getText().trim(),
            houseNumberField.getText().trim(),
            zipCodeField.getText().trim(),
            cityField.getText().trim(),
            countryField.getText().trim()
        );
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 