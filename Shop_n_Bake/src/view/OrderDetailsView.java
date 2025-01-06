package view;

import javax.swing.*;
import java.awt.*;

public class OrderDetailsView {

    private JFrame frame;
    private String orderId;

    public OrderDetailsView(String orderId) {
        this.orderId = orderId; // Pass order ID to fetch details
    }

    public void display() {
        frame = new JFrame("Order Details - Order #" + orderId);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        frame.add(panel);

        // Placeholder order details
        panel.add(new JLabel("Order ID:"));
        panel.add(new JLabel(orderId));
        panel.add(new JLabel("Customer Name:"));
        panel.add(new JLabel("John Doe")); // Replace with dynamic data
        panel.add(new JLabel("Status:"));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "Order Received", "Ingredients Being Prepared", "Cake Being Baked", "Ready for Pick-Up", "In Delivery"
        });
        panel.add(statusComboBox);

        panel.add(new JLabel("Delivery Employee:"));
        JComboBox<String> employeeComboBox = new JComboBox<>(new String[]{
                "Employee 1", "Employee 2", "Employee 3"
        });
        panel.add(employeeComboBox);

        // Save Button
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            // Save status and employee changes
            JOptionPane.showMessageDialog(frame, "Order updated successfully.");
            frame.dispose();
        });
        panel.add(saveButton);

        frame.setVisible(true);
    }
}