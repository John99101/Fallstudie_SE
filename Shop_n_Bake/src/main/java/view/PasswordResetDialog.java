package view;

import util.Database;
import util.UIManager;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class PasswordResetDialog {
    private final JDialog dialog;
    private JTextField emailField;

    public PasswordResetDialog(JFrame parent) {
        dialog = new JDialog(parent, UIManager.getText("Reset Password"), true);
        dialog.setSize(350, 250);
        initialize();
    }

    private void initialize() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIManager.BG_COLOR);

        // Title
        JLabel titleLabel = new JLabel(UIManager.getText("Reset Password"));
        titleLabel.setFont(UIManager.TITLE_FONT);
        titleLabel.setForeground(UIManager.FG_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email input panel
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setBackground(UIManager.BG_COLOR);
        JLabel emailLabel = new JLabel(UIManager.getText("Enter your email:"));
        emailLabel.setForeground(UIManager.FG_COLOR);
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 25));
        emailField.setMaximumSize(new Dimension(200, 25));
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);
        emailPanel.setMaximumSize(new Dimension(200, 50));
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(UIManager.BG_COLOR);
        
        JButton sendButton = UIManager.createStyledButton(UIManager.getText("Send Reset Link"));
        JButton cancelButton = UIManager.createStyledButton(UIManager.getText("Cancel"));

        sendButton.addActionListener(e -> handlePasswordReset());
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);
        dialog.getRootPane().setDefaultButton(sendButton);
    }

    private void handlePasswordReset() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, 
                UIManager.getText("Please enter your email."),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tempPassword = generateTempPassword();
        if (resetPassword(email, tempPassword)) {
            JOptionPane.showMessageDialog(dialog,
                UIManager.getText("Your temporary password is: ") + tempPassword + "\n" +
                UIManager.getText("Please change it after logging in."),
                UIManager.getText("Password Reset"),
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog,
                UIManager.getText("Email not found or error occurred."),
                UIManager.getText("Error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder temp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            temp.append(chars.charAt(random.nextInt(chars.length())));
        }
        return temp.toString();
    }

    private boolean resetPassword(String email, String tempPassword) {
        try (Connection conn = Database.getConnection()) {
            // First check if email exists
            String checkQuery = "SELECT user_id FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    return false;  // Email not found
                }
            }

            // Update password
            String updateQuery = "UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, tempPassword);
                updateStmt.setString(2, email);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void display() {
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }
} 