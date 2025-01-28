package view;

import model.Cake;
import util.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;

public class CakeEditDialog extends JDialog {
    private Cake cake;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField stockField;  // Added for stock management
    private boolean saved = false;

    public CakeEditDialog(JFrame parent, Cake cake) {
        super(parent, "Edit Cake", true);
        this.cake = cake;
        setupDialog();
    }

    private void setupDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 350);  // Increased height for stock field
        setLocationRelativeTo(getParent());

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(cake.getName(), 20);
        formPanel.add(nameField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(cake.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Price field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priceField = new JTextField(String.format("%.2f", cake.getPrice()), 10);
        formPanel.add(priceField, gbc);

        // Stock field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Stock:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        stockField = new JTextField(String.valueOf(cake.getStockAvailability()), 10);
        formPanel.add(stockField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateInputs()) {
                updateCake();
                saved = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format");
            return false;
        }

        try {
            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid stock format");
            return false;
        }

        return true;
    }

    private void updateCake() {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update cake details
                String updateCakeQuery = "UPDATE cakes SET name = ?, description = ?, price = ? WHERE cake_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateCakeQuery)) {
                    stmt.setString(1, nameField.getText().trim());
                    stmt.setString(2, descriptionArea.getText().trim());
                    stmt.setBigDecimal(3, new BigDecimal(priceField.getText()));
                    stmt.setInt(4, cake.getCakeId());
                    stmt.executeUpdate();
                }

                // Update stock
                String updateStockQuery = "UPDATE stock SET quantity = ? WHERE cake_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateStockQuery)) {
                    stmt.setInt(1, Integer.parseInt(stockField.getText()));
                    stmt.setInt(2, cake.getCakeId());
                    stmt.executeUpdate();
                }

                conn.commit();
                
                // Update local cake object
                cake.setName(nameField.getText().trim());
                cake.setDescription(descriptionArea.getText().trim());
                cake.setPrice(new BigDecimal(priceField.getText()));
                cake.setStockAvailability(Integer.parseInt(stockField.getText()));
                
                JOptionPane.showMessageDialog(this, "Cake updated successfully!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating cake: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
} 