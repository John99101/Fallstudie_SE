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
    private final User user;
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
        this.user = user;
        this.cart = new HashMap<>();
        this.totalLabel = new JLabel("Total: €0.00");
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Shop 'n' Bake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header with welcome message and logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Logout button
        JButton logoutButton = createAppleButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Simplified split pane with products and cart
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createProductPanel());
        splitPane.setRightComponent(createCartPanel());  // Changed from createOrderPanel
        splitPane.setDividerLocation(500);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);

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

    private void handleLogout() {
        frame.dispose();
        new LoginView().display();
    }

    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(UIManager.BG_COLOR);

        JLabel titleLabel = new JLabel(UIManager.getText("Available Products"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(UIManager.FG_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productPanel.add(titleLabel, BorderLayout.NORTH);

        // Changed to single column grid
        JPanel productsGrid = new JPanel(new GridLayout(0, 1, 10, 10));  // Changed from GridLayout(0, 2, 10, 10)
        productsGrid.setBackground(UIManager.BG_COLOR);
        
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM cakes";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Cake cake = new Cake(
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        1
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

    private void handleCheckout() {
        if (!cart.isEmpty()) {
            CheckoutView checkoutView = new CheckoutView(user, cart, "");
            checkoutView.display();  // Display checkout view first
            frame.dispose();  // Only dispose after new view is shown
        } else {
            JOptionPane.showMessageDialog(frame, "Your cart is empty!");
        }
    }

    private JPanel createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(UIManager.BG_COLOR);
        
        // Cart title
        JLabel titleLabel = new JLabel(UIManager.getText("Shopping Cart"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(UIManager.FG_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cartPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Cart items
        this.cartPanel = new JPanel();
        this.cartPanel.setBackground(UIManager.BG_COLOR);
        updateCartPanel();
        
        // Checkout button
        JButton checkoutButton = UIManager.createStyledButton("Checkout");
        checkoutButton.addActionListener(e -> handleCheckout());  // Use the new handler
        
        cartPanel.add(this.cartPanel, BorderLayout.CENTER);
        cartPanel.add(checkoutButton, BorderLayout.SOUTH);
        
        return cartPanel;
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 