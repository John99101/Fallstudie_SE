package view;

import javax.swing.*;
import java.awt.*;
import controller.OrderController;
import model.Cake;
import java.sql.*;
import model.Database;
import java.util.ArrayList;
import java.util.List;

public class CustomerView {
    private JFrame frame;
    private List<Cake> cakes = new ArrayList<>();

    public void display() {
        frame = new JFrame("Customer Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to the Bakery Shop!");
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Cake List Panel
        JPanel cakePanel = new JPanel();
        cakePanel.setLayout(new BoxLayout(cakePanel, BoxLayout.Y_AXIS));
        loadCakes(cakePanel);

        // Add scroll pane for cake list
        JScrollPane scrollPane = new JScrollPane(cakePanel);
        frame.add(scrollPane, BorderLayout.CENTER);

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
                
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");

                JLabel cakeLabel = new JLabel(name + " - $" + price);
                JButton addToCartButton = new JButton("Add to Cart");
                
                cakeItemPanel.add(cakeLabel);
                cakeItemPanel.add(addToCartButton);
                
                // Add description as tooltip
                cakeLabel.setToolTipText(description);
                
                panel.add(cakeItemPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading cakes: " + e.getMessage());
        }
    }
}
