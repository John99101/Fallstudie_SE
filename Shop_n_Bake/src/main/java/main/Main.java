package main;

import view.LoginView;
import util.UIManager;

public class Main {
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application with the login view
        javax.swing.SwingUtilities.invokeLater(() -> {
            UIManager.setupUI();  // Apply global styling
            new LoginView().display();
        });
    }
}