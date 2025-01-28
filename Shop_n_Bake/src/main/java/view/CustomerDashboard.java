package view;

import model.User;
import model.Cake;
import util.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerDashboard {
    private JFrame frame;
    private JPanel cakePanel;
    private ArrayList<Cake> cakes;
    private User user;

    public CustomerDashboard(User user) {
        this.user = user;
        this.cakes = new ArrayList<>();
        loadCakes();
    }

    public void display() {
        frame = new JFrame("Shop n' Bake - Customer Dashboard");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with scroll capability
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with welcome message and cart button
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Cakes display area
        cakePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        cakePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayCakes();

        // Add cake panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(cakePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Right panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // View Orders button
        JButton ordersButton = new JButton("My Orders");
        ordersButton.addActionListener(e -> viewOrders());
        
        // Cart button
        JButton cartButton = new JButton("Shopping Cart");
        cartButton.addActionListener(e -> viewCart());
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        rightPanel.add(ordersButton);
        rightPanel.add(cartButton);
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void loadCakes() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM cakes WHERE stock_availability > 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                cakes.clear();
                while (rs.next()) {
                    Cake cake = new Cake(
                        rs.getInt("cake_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_availability")
                    );
                    cakes.add(cake);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Error loading cakes: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCakes() {
        cakePanel.removeAll();

        for (Cake cake : cakes) {
            JPanel cakeCard = createCakeCard(cake);
            cakePanel.add(cakeCard);
        }

        cakePanel.revalidate();
        cakePanel.repaint();
    }

    private JPanel createCakeCard(Cake cake) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Cake name
        JLabel nameLabel = new JLabel(cake.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea descArea = new JTextArea(cake.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", cake.getPrice()));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to Cart button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart(cake));
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to card
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(addToCartButton);

        return card;
    }

    private void addToCart(Cake cake) {
        Map<Cake, Integer> cartItems = new HashMap<>();
        cartItems.put(cake, 1);
        new CheckoutView(user, cartItems, "").display();
    }

    private void viewCart() {
        Map<Cake, Integer> cartItems = new HashMap<>();  // Empty cart for now
        new CheckoutView(user, cartItems, "").display();
    }

    private void viewOrders() {
        // TODO: Implement view orders functionality
        JOptionPane.showMessageDialog(frame, "View orders functionality coming soon!");
    }

    private void logout() {
        frame.dispose();
        new LoginView().display();
    }
} 