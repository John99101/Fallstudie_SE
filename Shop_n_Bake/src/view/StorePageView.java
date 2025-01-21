package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import model.Cake;
import model.Database;

public class StorePageView {
    private JFrame frame;
    private JPanel cakePanel;
    private ArrayList<Cake> cakes;
    private int userId;
    private boolean isEmployee;

    public StorePageView(int userId) {
        this.userId = userId;
        this.cakes = new ArrayList<>();
        checkIfEmployee();
        loadCakes();
    }

    private void checkIfEmployee() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT is_employee FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                this.isEmployee = rs.getBoolean("is_employee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void display() {
        frame = new JFrame("Shop n' Bake - Store");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with scroll capability
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
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

        // Title
        JLabel titleLabel = new JLabel("Welcome to Shop n' Bake", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchCakes(searchField.getText()));
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void loadCakes() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM cakes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Cake cake = new Cake(
                    rs.getInt("cake_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    1  // stock availability - using 1 as default instead of true
                );
                cakes.add(cake);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading cakes: " + e.getMessage());
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

        // Button - different for employee vs customer
        JButton actionButton;
        if (isEmployee) {
            actionButton = new JButton("Edit Cake");
            actionButton.addActionListener(e -> editCake(cake));
        } else {
            actionButton = new JButton("Add to Cart");
            actionButton.addActionListener(e -> addToCart(cake));
        }
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to card
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(actionButton);

        return card;
    }

    private void addToCart(Cake cake) {
        // TODO: Implement add to cart functionality
        JOptionPane.showMessageDialog(frame, cake.getName() + " added to cart!");
    }

    private void editCake(Cake cake) {
        CakeEditDialog dialog = new CakeEditDialog(frame, cake);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            // Refresh the display
            loadCakes();
            displayCakes();
        }
    }

    private void searchCakes(String query) {
        ArrayList<Cake> filteredCakes = new ArrayList<>();
        for (Cake cake : cakes) {
            if (cake.getName().toLowerCase().contains(query.toLowerCase()) ||
                cake.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredCakes.add(cake);
            }
        }
        cakes = filteredCakes;
        displayCakes();
    }
}