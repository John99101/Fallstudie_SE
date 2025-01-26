package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.Database;
import util.UIManager;

public class SettingsView {
    private JFrame frame;
    private final int userId;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JFrame parentFrame;

    public SettingsView(int userId) {
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Settings");
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UIManager.BG_COLOR);

        // Email section
        JPanel emailPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        emailPanel.setBackground(UIManager.BG_COLOR);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(UIManager.FG_COLOR);
        emailField = new JTextField(20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        // Password change section
        JPanel passwordPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        passwordPanel.setBackground(UIManager.BG_COLOR);
        
        JLabel currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setForeground(UIManager.FG_COLOR);
        currentPasswordField = new JPasswordField(20);
        
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setForeground(UIManager.FG_COLOR);
        newPasswordField = new JPasswordField(20);
        
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setForeground(UIManager.FG_COLOR);
        confirmPasswordField = new JPasswordField(20);

        passwordPanel.add(currentPassLabel);
        passwordPanel.add(currentPasswordField);
        passwordPanel.add(newPassLabel);
        passwordPanel.add(newPasswordField);
        passwordPanel.add(confirmPassLabel);
        passwordPanel.add(confirmPasswordField);

        // Add language selection
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setBackground(UIManager.BG_COLOR);
        JLabel languageLabel = new JLabel(UIManager.getText("Language:"));
        languageLabel.setForeground(UIManager.FG_COLOR);
        
        JComboBox<String> languageCombo = new JComboBox<>(new String[]{"Deutsch", "English"});
        languageCombo.setSelectedIndex(UIManager.isGerman ? 0 : 1);
        languageCombo.addActionListener(e -> {
            UIManager.isGerman = languageCombo.getSelectedIndex() == 0;
            // Refresh all open windows
            SwingUtilities.updateComponentTreeUI(frame);
            if (parentFrame != null) {
                SwingUtilities.updateComponentTreeUI(parentFrame);
            }
        });
        
        languagePanel.add(languageLabel);
        languagePanel.add(languageCombo);
        
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(languagePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(UIManager.BG_COLOR);
        
        JButton saveButton = UIManager.createStyledButton("Save Changes");
        JButton cancelButton = UIManager.createStyledButton("Cancel");

        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        frame.add(mainPanel);
        loadUserData();
    }

    private void loadUserData() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT email FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        emailField.setText(rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading user data: " + e.getMessage());
        }
    }

    private void saveChanges() {
        String newEmail = emailField.getText().trim();
        String currentPass = new String(currentPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(frame, "New passwords don't match!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            // Verify current password
            String verifyQuery = "SELECT password FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(verifyQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPass = rs.getString("password");
                        if (!currentPass.equals(storedPass)) {
                            JOptionPane.showMessageDialog(frame, "Current password is incorrect!");
                            return;
                        }
                    }
                }
            }

            // Update user data
            StringBuilder updateQuery = new StringBuilder("UPDATE users SET email = ?");
            if (!newPass.isEmpty()) {
                updateQuery.append(", password = ?");
            }
            updateQuery.append(" WHERE user_id = ?");

            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery.toString())) {
                pstmt.setString(1, newEmail);
                if (!newPass.isEmpty()) {
                    pstmt.setString(2, newPass);
                    pstmt.setInt(3, userId);
                } else {
                    pstmt.setInt(2, userId);
                }
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Settings updated successfully!");
                frame.dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving changes: " + e.getMessage());
        }
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
} 