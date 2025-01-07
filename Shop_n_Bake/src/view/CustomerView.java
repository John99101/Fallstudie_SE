package view;

import javax.swing.*;
import java.awt.*;
import controller.OrderController;
import model.Cake;
import java.sql.*;
import model.Database;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CustomerView {
    private JFrame frame;
    private List<Cake> cakes = new ArrayList<>();
    private Map<Integer, Integer> cart = new HashMap<>(); // cakeId -> quantity
    private JLabel cartLabel;
    private double totalPrice = 0.0;
    private int userId;

    public CustomerView(int userId) {
        this.userId = userId;
    }

    public void display() {
        frame = new JFrame("Customer Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to the Bakery Shop!");
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Main Panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Cake List Panel (left side)
        JPanel cakePanel = new JPanel();
        cakePanel.setLayout(new BoxLayout(cakePanel, BoxLayout.Y_AXIS));
        loadCakes(cakePanel);
        
        // Cart Panel (right side)
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartLabel = new JLabel("Cart is empty");
        cartPanel.add(cartLabel);
        
        // Add Checkout Button
        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.addActionListener(e -> proceedToCheckout());
        cartPanel.add(checkoutButton);

        // Add panels to main panel
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JScrollPane(cakePanel), gbc);

        gbc.weightx = 0.3;
        gbc.gridx = 1;
        mainPanel.add(cartPanel, gbc);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void loadCakes(JPanel panel) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM cakes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JPanel cakeItemPanel = new JPanel();
                cakeItemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                
                int cakeId = rs.getInt("cake_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");

                Cake cake = new Cake(cakeId, name, description, price, rs.getInt("stock_availability"));
                cakes.add(cake);

                JLabel cakeLabel = new JLabel(name + " - $" + price);
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
                JButton addToCartButton = new JButton("Add to Cart");
                
                addToCartButton.addActionListener(e -> addToCart(cake, (Integer)quantitySpinner.getValue()));
                
                cakeItemPanel.add(cakeLabel);
                cakeItemPanel.add(quantitySpinner);
                cakeItemPanel.add(addToCartButton);
                
                cakeLabel.setToolTipText(description);
                
                panel.add(cakeItemPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading cakes: " + e.getMessage());
        }
    }

    private void addToCart(Cake cake, int quantity) {
        cart.put(cake.getCakeId(), cart.getOrDefault(cake.getCakeId(), 0) + quantity);
        totalPrice += cake.getPrice() * quantity;
        updateCartDisplay();
        JOptionPane.showMessageDialog(frame, quantity + "x " + cake.getName() + " added to cart!");
    }

    private void updateCartDisplay() {
        StringBuilder cartText = new StringBuilder("<html>Cart Contents:<br/>");
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Cake cake = cakes.stream()
                .filter(c -> c.getCakeId() == entry.getKey())
                .findFirst()
                .orElse(null);
            if (cake != null) {
                cartText.append(cake.getName())
                    .append(" x")
                    .append(entry.getValue())
                    .append(" - $")
                    .append(String.format("%.2f", cake.getPrice() * entry.getValue()))
                    .append("<br/>");
            }
        }
        cartText.append("<br/>Total: $").append(String.format("%.2f", totalPrice)).append("</html>");
        cartLabel.setText(cartText.toString());
    }

    private void proceedToCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Your cart is empty!");
            return;
        }
        new CheckoutView(cart, totalPrice, cakes, userId).display();
    }
}
