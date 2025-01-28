package view;

import model.User;
import util.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterView {
    private JFrame frame;
    private JTextField emailField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox employeeCheckBox;
    private final Color HOVER_COLOR = new Color(70, 130, 180);
    private final Color NORMAL_COLOR = Color.BLACK;

    public RegisterView() {
        initialize();
    }

    public void display() {
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Email
        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Employee Checkbox
        employeeCheckBox = new JCheckBox("Register as Employee");
        employeeCheckBox.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(employeeCheckBox, gbc);

        // Register Button
        JLabel registerButton = createHoverLabel("Register");
        gbc.gridy++;
        mainPanel.add(registerButton, gbc);

        // Back to Login Button
        JLabel backButton = createHoverLabel("Back to Login");
        gbc.gridy++;
        mainPanel.add(backButton, gbc);

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRegister();
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new LoginView().display();
            }
        });

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
    }

    private JLabel createHoverLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(NORMAL_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(NORMAL_COLOR);
            }
        });
        
        return label;
    }

    private void handleRegister() {
        String email = emailField.getText();
        String name = nameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isEmployee = employeeCheckBox.isSelected();

        if (!validateInputs(email, name, password, confirmPassword)) {
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert into users table
                String userQuery = "INSERT INTO users (email, password, name, is_employee) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, email);
                    pstmt.setString(2, password);
                    pstmt.setString(3, name);
                    pstmt.setBoolean(4, isEmployee);
                    
                    pstmt.executeUpdate();

                    // If employee, create employee record
                    if (isEmployee) {
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int userId = generatedKeys.getInt(1);
                                String empQuery = "INSERT INTO employees (user_id, position, hire_date) VALUES (?, 'New Employee', CURRENT_DATE)";
                                try (PreparedStatement empStmt = conn.prepareStatement(empQuery)) {
                                    empStmt.setInt(1, userId);
                                    empStmt.executeUpdate();
                                }
                            }
                        }
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(frame, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new LoginView().display();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Registration failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs(String email, String name, String password, String confirmPassword) {
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
