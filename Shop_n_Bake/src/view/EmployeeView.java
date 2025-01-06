package view;

import javax.swing.*;
import java.awt.*;
import controller.StockController;

public class EmployeeView {
    public void display() {
        JFrame frame = new JFrame("Employee Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components: Orders list, Stock, and Order management
        JLabel welcomeLabel = new JLabel("Welcome, Employee!");
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Example: Add Order List
        JPanel orderPanel = new JPanel();
        orderPanel.add(new JLabel("Order List"));
        frame.add(orderPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
