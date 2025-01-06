package view;

import controller.UserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterView {
    private JFrame frame;

    public void display() {
        frame = new JFrame("Register");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 20, 80, 25);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(100, 20, 165, 25);
        panel.add(emailText);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 50, 165, 25);
        panel.add(nameText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 80, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 80, 165, 25);
        panel.add(passwordText);

        JLabel keyLabel = new JLabel("Registration Key (Employee):");
        keyLabel.setBounds(10, 110, 200, 25);
        panel.add(keyLabel);

        JTextField keyText = new JTextField(20);
        keyText.setBounds(200, 110, 165, 25);
        panel.add(keyText);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(10, 140, 120, 25);
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailText.getText();
                String name = nameText.getText();
                String password = new String(passwordText.getPassword());
                String key = keyText.getText();
                boolean isEmployee = !key.isEmpty();

                UserController userController = new UserController();

                if (userController.isEmailRegistered(email)) {
                    JOptionPane.showMessageDialog(null, "Email is already registered. Please use another email.");
                    return;
                }

                boolean success = userController.registerUser(email, name, password, isEmployee, key);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                    frame.dispose();
                    new LoginView().display();
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
                }
            }
        });
    }
}
