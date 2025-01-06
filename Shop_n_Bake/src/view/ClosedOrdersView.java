package view;

import javax.swing.*;

public class ClosedOrdersView {

    private JFrame frame;

    public void display() {
        frame = new JFrame("Closed Orders");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Placeholder UI for viewing closed orders
        JOptionPane.showMessageDialog(frame, "Closed orders functionality coming soon!");
        frame.setVisible(true);
    }
}