package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeView {

    private JFrame frame;

    public void display() {
        frame = new JFrame("Employee Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        frame.add(panel);

        // Orders Button
        JButton ordersButton = new JButton("Orders");
        ordersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrdersView().display();
            }
        });
        panel.add(ordersButton);

        // Store Page Button
        JButton storePageButton = new JButton("Store Page");
        storePageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StorePageView().display();
            }
        });
        panel.add(storePageButton);

        // Stock Button
        JButton stockButton = new JButton("Stock");
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StockView().display();
            }
        });
        panel.add(stockButton);

        // Closed Orders Button
        JButton closedOrdersButton = new JButton("Closed Orders");
        closedOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClosedOrdersView().display();
            }
        });
        panel.add(closedOrdersButton);

        frame.setVisible(true);
    }
}