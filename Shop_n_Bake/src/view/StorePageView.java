package view;

import javax.swing.*;

public class StorePageView {

    private JFrame frame;

    public void display() {
        frame = new JFrame("Store Page");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Placeholder UI for managing cakes
        JOptionPane.showMessageDialog(frame, "Store page functionality coming soon!");
        frame.setVisible(true);
    }
}