package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

public class OrdersView {

    private JFrame frame;
    private JTable ordersTable;

    public void display() {
        frame = new JFrame("Orders");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        // Table for Orders
        String[] columnNames = {"Order ID", "Customer Name", "Status", "Pick-Up/Delivery"};
        Object[][] data = {
                // Placeholder data
                {"1", "John Doe", "Order Received", "Delivery"},
                {"2", "Jane Smith", "Being Baked", "Pick-Up"}
        };
        ordersTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        JButton viewOrderButton = new JButton("View Order");
        viewOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderId = ordersTable.getValueAt(selectedRow, 0).toString();
                    new OrderDetailsView(orderId).display();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an order to view.");
                }
            }
        });
        buttonsPanel.add(viewOrderButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}