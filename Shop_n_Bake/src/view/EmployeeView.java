package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeView {

    private JFrame frame;
    private int userId;

    public EmployeeView(int userId) {
        this.userId = userId;
    }

    public void display() {
        frame = new JFrame("Employee Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1, 10, 10));
        frame.setLocationRelativeTo(null);

        // Create buttons
        JButton ordersButton = new JButton("Orders");
        JButton storePageButton = new JButton("Store Page");
        JButton stockButton = new JButton("Stock");
        JButton closedOrdersButton = new JButton("Closed Orders");

        // Add action listeners
        ordersButton.addActionListener(e -> {
            new OrdersView().display();
        });

        storePageButton.addActionListener(e -> {
            new StorePageView(userId).display();
        });

        stockButton.addActionListener(e -> {
            // TODO: Implement stock view
        });

        closedOrdersButton.addActionListener(e -> {
            // TODO: Implement closed orders view
        });

        // Add buttons to frame
        frame.add(ordersButton);
        frame.add(storePageButton);
        frame.add(stockButton);
        frame.add(closedOrdersButton);

        frame.setVisible(true);
    }
}