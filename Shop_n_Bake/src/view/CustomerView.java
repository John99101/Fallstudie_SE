package view;

import javax.swing.*;
import java.awt.*;
import controller.OrderController;

public class CustomerView {
    public void display() {
        JFrame frame = new JFrame("Customer Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components: Cake list, Basket, and Order details
        JLabel welcomeLabel = new JLabel("Welcome to the Bakery Shop!");
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Example: Add Cake List
        JPanel cakePanel = new JPanel();
        cakePanel.add(new JLabel("Cake List"));
        frame.add(cakePanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
