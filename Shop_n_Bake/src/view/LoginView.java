package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import model.Database;
import controller.UserController;
import model.User;

public class LoginView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public void display() {
        frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Email:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 80, 100, 25);
        panel.add(registerButton);

        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                System.out.println("Login attempt - Email: " + username); // Debug print
                
                try (Connection conn = Database.getConnection()) {
                    System.out.println("Database connected successfully"); // Debug print
                    
                    String sql = "SELECT id, is_employee FROM users WHERE email = ? AND password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    
                    System.out.println("Executing query..."); // Debug print
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("User found!"); // Debug print
                        int userId = rs.getInt("id");
                        boolean isEmployee = rs.getBoolean("is_employee");
                        
                        frame.dispose();
                        if (isEmployee) {
                            new EmployeeView(userId).display();
                        } else {
                            new CustomerView(userId).display();
                        }
                    } else {
                        System.out.println("No user found"); // Debug print
                        JOptionPane.showMessageDialog(frame, "Invalid username or password");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.err.println("SQL Error: " + ex.getMessage()); // Debug print
                    JOptionPane.showMessageDialog(frame, "Error during login: " + ex.getMessage());
                }
            }
        });

        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RegisterView().display();
            }
        });
    }
}
