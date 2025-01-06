package view;

import javax.swing.*;

public class StockView {

    private JFrame frame;

    public void display() {
        frame = new JFrame("Stock Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Placeholder UI for managing stock
        JOptionPane.showMessageDialog(frame, "Stock management functionality coming soon!");
        frame.setVisible(true);
    }
}