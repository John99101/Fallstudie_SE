package view;

import model.User;
import util.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private final Color HOVER_COLOR = new Color(70, 130, 180);
    private final Color NORMAL_COLOR = Color.BLACK;

    public LoginView() {
        initialize();
    }

    public void display() {
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Login");
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

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Login Button
        JLabel loginButton = createHoverLabel("Login");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        // Register Button
        JLabel registerButton = createHoverLabel("Register");
        gbc.gridy++;
        mainPanel.add(registerButton, gbc);

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLogin();
            }
        });

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new RegisterView().display();
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

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        email,
                        password,
                        email,
                        rs.getString("name"),
                        rs.getString("role").equals("employee")
                    );
                    
                    frame.dispose();
                    if (user.isEmployee()) {
                        new EmployeeView(user).display();
                    } else {
                        new CustomerDashboard(user).display();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().display());
    }
}
