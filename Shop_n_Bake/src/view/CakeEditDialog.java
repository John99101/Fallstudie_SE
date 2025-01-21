package view;

import model.Cake;
import model.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CakeEditDialog extends JDialog {
    private Cake cake;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private boolean saved = false;

    public CakeEditDialog(JFrame parent, Cake cake) {
        super(parent, "Edit Cake", true);
        this.cake = cake;
        setupDialog();
    }

    private void setupDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 300);
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

        return true;
    }

    private void updateCake() {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE cakes SET name = ?, description = ?, price = ? WHERE cake_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
            pstmt.setInt(4, cake.getCakeId());
            
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                // Update the cake object
                cake.setName(nameField.getText().trim());
                cake.setDescription(descriptionArea.getText().trim());
                cake.setPrice(Double.parseDouble(priceField.getText()));
                
                JOptionPane.showMessageDialog(this, "Cake updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating cake: " + e.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
} 