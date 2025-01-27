package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.text.NumberFormat;
import java.util.Base64;
import java.net.URL;
import view.LoginView;

public class UIManager {
    public static boolean isDarkMode = false;
    public static boolean isGerman = true;  // Default Deutsch

    // Theme Colors
    private static final Color LIGHT_BG = new Color(250, 250, 250);
    private static final Color DARK_BG = new Color(33, 33, 33);
    private static final Color LIGHT_FG = new Color(33, 33, 33);
    private static final Color DARK_FG = Color.WHITE;  // Changed to pure white
    private static final Color LIGHT_PRIMARY = new Color(66, 133, 244);
    private static final Color DARK_PRIMARY = new Color(138, 180, 248);
    private static final Color BUTTON_HOVER = new Color(139, 69, 19);  // Brown color for hover
    
    public static Color BG_COLOR = LIGHT_BG;
    public static Color FG_COLOR = LIGHT_FG;
    public static Color PRIMARY_COLOR = LIGHT_PRIMARY;

    // Bakery-themed colors
    public static final Color LIGHT_ACCENT = new Color(198, 155, 123); // Latte
    public static final Color LIGHT_HOVER = new Color(178, 135, 103); // Darker latte
    public static final Color LIGHT_BORDER = new Color(227, 214, 205); // Cream
    public static final Color LIGHT_SURFACE = new Color(249, 249, 249); // Light surface

    // Dark mode colors
    public static final Color DARK_ACCENT = new Color(198, 155, 123); // Latte
    public static final Color DARK_HOVER = new Color(218, 175, 143); // Light latte
    public static final Color DARK_BORDER = new Color(84, 60, 46);   // Coffee brown
    public static final Color DARK_SURFACE = new Color(35, 30, 28);  // Darker roast

    // Modern icons (Material Symbols Rounded style)
    public static final String CART_ICON = """
        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
            <path d="M280-80q-33 0-56.5-23.5T200-160q0-33 23.5-56.5T280-240q33 0 56.5 23.5T360-160q0 33-23.5 56.5T280-80Zm400 0q-33 0-56.5-23.5T600-160q0-33 23.5-56.5T680-240q33 0 56.5 23.5T760-160q0 33-23.5 56.5T680-80ZM246-720l96 200h280l110-200H246Zm-38-80h590q23 0 35 20.5t1 41.5L692-482q-11 20-29.5 31T622-440H324l-44 80h480v80H280q-45 0-68-39.5t-2-78.5l54-98-144-304H40v-80h130l38 80Zm134 280h280-280Z"/>
        </svg>
        """;
    
    public static final String CHECKOUT_ICON = """
        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
            <path d="M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z"/>
        </svg>
        """;
        
    public static final String ORDERS_ICON = """
        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
            <path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z"/>
        </svg>
        """;

    // SVG icons as strings (using material design icons)
    public static final String MOON_ICON = """
        <svg xmlns="http://www.w3.org/2000/svg" height="24" width="24" fill="white">
            <path d="M12 21q-3.75 0-6.375-2.625T3 12q0-3.75 2.625-6.375T12 3q.35 0 .688.025.337.025.662.075-1.025.725-1.637 1.887Q11.1 6.15 11.1 7.5q0 2.25 1.575 3.825Q14.25 12.9 16.5 12.9q1.375 0 2.525-.613 1.15-.612 1.875-1.637.05.325.075.662Q21 11.65 21 12q0 3.75-2.625 6.375T12 21Z"/>
        </svg>
        """;
    
    public static final String SUN_ICON = """
        <svg xmlns="http://www.w3.org/2000/svg" height="24" width="24" fill="white">
            <path d="M12 17q-2.075 0-3.537-1.463Q7 14.075 7 12t1.463-3.538Q9.925 7 12 7t3.538 1.462Q17 9.925 17 12q0 2.075-1.462 3.537Q14.075 17 12 17ZM2 13q-.425 0-.712-.288Q1 12.425 1 12t.288-.713Q1.575 11 2 11h2q.425 0 .713.287Q5 11.575 5 12t-.287.712Q4.425 13 4 13Zm18 0q-.425 0-.712-.288Q19 12.425 19 12t.288-.713Q19.575 11 20 11h2q.425 0 .712.287.288.288.288.713t-.288.712Q22.425 13 22 13Zm-8-8q-.425 0-.712-.288Q11 4.425 11 4V2q0-.425.288-.713Q11.575 1 12 1t.713.287Q13 1.575 13 2v2q0 .425-.287.712Q12.425 5 12 5Zm0 18q-.425 0-.712-.288Q11 22.425 11 22v-2q0-.425.288-.712Q11.575 19 12 19t.713.288Q13 19.575 13 20v2q0 .425-.287.712Q12.425 23 12 23ZM5.65 7.05 4.575 6q-.3-.275-.3-.688 0-.412.3-.712t.725-.3q.4 0 .7.3L7.05 5.65q.275.3.275.7 0 .4-.275.7-.275.3-.687.3-.413 0-.713-.3Zm12.7 12.725L17.3 18.7q-.275-.3-.275-.7 0-.4.275-.7.275-.3.688-.3.412 0 .712.3l1.075 1.05q.3.275.3.688 0 .412-.3.712t-.725.3q-.4 0-.7-.275ZM17.3 7.05q-.3-.275-.3-.675 0-.4.3-.7l1.05-1.075q.275-.3.688-.3.412 0 .712.3t.3.725q0 .4-.3.7L18.7 7.05q-.3.275-.7.275-.4 0-.7-.275ZM4.575 19.775q-.3-.275-.3-.688 0-.412.3-.712L5.65 17.3q.3-.275.7-.275.4 0 .7.275.275.3.275.7 0 .4-.275.7l-1.075 1.075q-.275.3-.687.3-.413 0-.713-.3Z"/>
        </svg>
        """;

    // Fonts
    public static final Font NORMAL_FONT = new Font(".AppleSystemUIFont", Font.PLAIN, 13);
    public static final Font HEADER_FONT = new Font(".AppleSystemUIFont", Font.BOLD, 15);
    public static final Font TITLE_FONT = new Font(".AppleSystemUIFont", Font.BOLD, 20);
    
    // Borders
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(LIGHT_ACCENT, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );
    
    // Modern icon paths
    private static final String[] REQUIRED_ICONS = {
        "/icons/moon.svg",
        "/icons/sun.svg",
        "/icons/cart.svg",
        "/icons/cart-plus.svg",
        "/icons/edit.svg",
        "/icons/history.svg"
    };

    // Currency formatter
    private static final NumberFormat germanFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private static final NumberFormat englishFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    
    public static String formatCurrency(double amount) {
        return isGerman ? germanFormatter.format(amount) : englishFormatter.format(amount);
    }

    // Deutsche Texte
    private static final Map<String, String> translations = new HashMap<>() {{
        // Allgemein
        put("Logout", "Abmelden");
        put("Settings", "Einstellungen");
        put("Dark Mode", "Dunkelmodus");
        
        // Customer Dashboard
        put("Shopping Cart", "Warenkorb");
        put("Add to Cart", "In den Warenkorb");
        put("Remove", "Entfernen");
        put("Proceed to Checkout", "Zur Kasse");
        put("My Orders", "Meine Bestellungen");
        put("Total", "Gesamt");
        put("Delivery Type", "Lieferart");
        put("Pickup", "Abholung");
        put("Delivery", "Lieferung");
        put("Payment Method", "Zahlungsmethode");
        put("Address", "Adresse");
        
        // Employee Dashboard
        put("Employee Dashboard", "Mitarbeiter-Dashboard");
        put("Manage Products", "Produkte verwalten");
        put("Add Product", "Produkt hinzufügen");
        put("Edit Product", "Produkt bearbeiten");
        put("Delete Product", "Produkt löschen");
        put("Order Management", "Bestellungsverwaltung");
        put("Product Name", "Produktname");
        put("Description", "Beschreibung");
        put("Price", "Preis");
        put("Available", "Verfügbar");
    }};

    public static String getText(String key) {
        return isGerman ? translations.getOrDefault(key, key) : key;
    }

    public static void setupUI() {
        try {
            // Set system look and feel
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
            
            // Set default font
            setUIFont(new FontUIResource(NORMAL_FONT));
            
            // Customize components
            javax.swing.UIManager.put("Button.background", LIGHT_ACCENT);
            javax.swing.UIManager.put("Button.foreground", FG_COLOR);
            javax.swing.UIManager.put("Button.font", NORMAL_FONT);
            javax.swing.UIManager.put("Button.margin", new Insets(5, 15, 5, 15));
            javax.swing.UIManager.put("Button.border", BorderFactory.createLineBorder(LIGHT_ACCENT.darker(), 1));
            
            javax.swing.UIManager.put("Panel.background", BG_COLOR);
            javax.swing.UIManager.put("Label.foreground", FG_COLOR);
            javax.swing.UIManager.put("TextField.font", NORMAL_FONT);
            javax.swing.UIManager.put("TextField.background", isDarkMode ? DARK_SURFACE : LIGHT_SURFACE);
            javax.swing.UIManager.put("TextField.foreground", FG_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(getText(text));
        button.setForeground(FG_COLOR);
        button.setBackground(BG_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(FG_COLOR);
            }
        });
        
        return button;
    }
    
    public static JButton createIconButton() {
        JButton button = new JButton(isDarkMode ? "☀️" : "🌙");
        button.setFont(NORMAL_FONT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBackground(null);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    public static JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(PANEL_BORDER, title));
        panel.setBackground(isDarkMode ? DARK_SURFACE : LIGHT_SURFACE);
        return panel;
    }
    
    private static void setUIFont(FontUIResource f) {
        java.util.Enumeration<Object> keys = javax.swing.UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = javax.swing.UIManager.get(key);
            if (value instanceof FontUIResource) {
                javax.swing.UIManager.put(key, f);
            }
        }
    }

    // Base64 encoded PNG icons
    private static final String EYE_OPEN_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAABP0lEQVR4nO2UPUsDQRCGn4gWFhYWFhYW/gULCwsLC/+AjYWFhYWFhYWFhYWFhYWFhUVAMIQQQgwhxBBCCDEcIYQQQwj7ysFy7OF9yQVB8IVld2fm2Z2ZnYU//lq5KtWBY+AK6AAvwBvQBy6BY6BW1agJPALfOc8D0MgTOwAGEYEkBsCelVoCbjICzgnsA8vAHNAC7hPrV8BSVuVJwRvgCNiwyBrAcUzMYjkl0I0Zbhv7FrBr3+PApcnMSiVhJ2ZWssgKwIk9T+1ZtLFrYNNkZ6WSsBcza1pkReBM8yayYrKzUknYj5mtWmQ7wL3mTWTNZGelkvApZrZmkW0BdzGZdZOdlUrCz5jZukVWAk5N5gtYMdlZqSQcxszKFllgKz8AF8BWrFpVKgmHMbMKsA2sA4vW3wrQjgnlppLwt/kBYbaHoZC8JpIAAAAASUVORK5CYII=";
    
    private static final String EYE_CLOSED_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAABZElEQVR4nO2UP0vDUBTFf0UHBwcHBwcHBz+Cg4ODg4OfwMXBwcHBwcHBwcHBwcHBwUEQBBGRIiIiIiIiIiIiIiIiIuJ5cgtpSGPyBweHHHh5955z7r03cI9/HtPAKrADHAFnwCVwDZwDB8AmMDdqozvgK+F5B7ZTmUXgKUUgigdgIU1sGXhNCXgPrAOzQBk4jKy/AEtZK48L3gKbQMkUmwc2gGZETBcbxwW7EcNtw74C1OxbBc5NZlBqEHYiZiWTLAMc27MVWT8x2UGpQdiPmJVNshxwZN9OZL1ssoNSg/ApYrZikuWBQ3s+2LdgssNS/bAfMVs1yWrAjT0fgTmTHZbqh4OI2ZpJVgWu7NkC8iY7LNUPX7ILmGQ14NK+70DBZP+iEjCJ2HvWgApQtDhLQCOy/maSZcVnxKwMbNn7vL0XgJ2IUG4qDt8jZgVgHSgBk5bfPFCPCKVS/zF+AGZQZ/OaCQduAAAAAElFTkSuQmCC";

    public static ImageIcon createThemedIcon(String name) {
        try {
            if ("eye-open".equals(name)) {
                return new ImageIcon(Base64.getDecoder().decode(EYE_OPEN_BASE64));
            } else if ("eye-closed".equals(name)) {
                return new ImageIcon(Base64.getDecoder().decode(EYE_CLOSED_BASE64));
            } else if ("moon".equals(name)) {
                return new ImageIcon(Base64.getDecoder().decode(MOON_ICON_BASE64));
            } else if ("sun".equals(name)) {
                return new ImageIcon(Base64.getDecoder().decode(SUN_ICON_BASE64));
            }
            
            // For other icons, create a simple placeholder
            BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(FG_COLOR);
            g2d.drawRect(2, 2, 16, 16);
            g2d.dispose();
            return new ImageIcon(img);
        } catch (Exception e) {
            // Return a simple placeholder on error
            BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
            return new ImageIcon(img);
        }
    }

    // Add Base64 encoded icons for theme toggle
    private static final String MOON_ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAABZklEQVR4nO2UPUvDUBSGn4gODg4ODg4ODn4EBwcHBwc/gYuDg4ODg4ODg4ODg4ODg4IgCCIiIiIiIiIiIiIiIiLieXILaUhj8gcHhxx4effec869N3CPfx7TwCqwAxwBZ8AlcA2cAwfAJjA3aqM74CvheQe2U5lF4ClFIIoHYCFNbBl4TQl4D6wDs0AZOIysvwBLWZXHBW+BTaBkis0DG0AzIqaLjeOC3YjhtrGvADX7VoFzk5kVSsJOxKxskQW28h2wC8zHqg0KJeEwZjYOLABVYMr6KwHNiFBuKgnfImZ5YA6YBfLWXx6oR4RyU0n4GjErAYv2PWvvOWA9IpSbSsKPmNmMSVYGdu35AkyY7H+hJOzHzKZMsiqwZ89bIG+yg0JJ2I2YVUyyGnBtz0eg0FVoUCgJOxGzGZOsCpzY8w7Id4QGhZKwFzGbBOaBKjBu/U0CjYhQbioJfxs/gw2Jm5Cz4UAAAAAASUVORK5CYII=";

    private static final String SUN_ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAABsElEQVR4nO2UO0scURiGH0UWQgghhBBCCCH4FywsLCwsLPwDNhYWFhYWFhYWFhYWFhYWFgEhLCGEEEIIIYQQQgghhBBCOI+cYVnW3fXsJk3eZmDm+873PnPmnPngP/46JWAOWAYagSZwD1wBW8AiUBm20SXwkfC8AYtDmRWBxwyBKO6A+SyxaeCpS8B7YBaYAMaBjcj6IzA1qPKk4DWwBIyZYpPAPHAREbNYjgv2IoZLxr4AVOxbBrZNZlAoCbsRs5JJlgM27dmOrG+b7KBQEvYjZmWTLAes2bcbWS+b7KBQEvYiZlMm2QSwb897YNJkB4WSsB8xmzbJKsCRPR+AgsluCf0XSsJBxKxskQW28h2wC8zHqg0KJeEwZjYOLABVYMr6KwHNiFBuKgnfImZ5YA6YBfLWXx6oR4RyU0n4GjErAYv2PWvvOWA9IpSbSsKPmNmMSVYGdu35AkyY7H+hJOzHzKZMsiqwZ89bIG+yg0JJ2I2YVUyyGnBtz0eg0FVoUCgJOxGzGZOsCpzY8w7Id4QGhZKwFzGbBOaBKjBu/U0CjYhQbioJfxs/gw2Jm5Cz4UAAAAAASUVORK5CYII=";

    public static JButton createLanguageButton() {
        JButton langButton = new JButton(isGerman ? "EN" : "DE");
        langButton.setFocusPainted(false);
        langButton.setBorderPainted(false);
        langButton.setBackground(BG_COLOR);
        langButton.setForeground(FG_COLOR);
        langButton.addActionListener(e -> {
            isGerman = !isGerman;
            langButton.setText(isGerman ? "EN" : "DE");
            updateAllFrames();
        });
        return langButton;
    }

    public static JButton createThemeToggleButton() {
        JButton themeButton = new JButton(isDarkMode ? "☀️" : "🌙");
        themeButton.setFocusPainted(false);
        themeButton.setBorderPainted(false);
        themeButton.setBackground(BG_COLOR);
        themeButton.setForeground(FG_COLOR);
        themeButton.addActionListener(e -> toggleTheme());
        return themeButton;
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        BG_COLOR = isDarkMode ? DARK_BG : LIGHT_BG;
        FG_COLOR = isDarkMode ? DARK_FG : LIGHT_FG;
        PRIMARY_COLOR = isDarkMode ? DARK_PRIMARY : LIGHT_PRIMARY;
        updateAllFrames();
    }

    private static void updateAllFrames() {
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                updateComponentColors(frame.getContentPane());
                frame.repaint();
                frame.revalidate();
            }
        }
    }

    private static void updateComponentColors(Component comp) {
        comp.setBackground(BG_COLOR);
        comp.setForeground(FG_COLOR);
        
        if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            button.setBackground(BG_COLOR);
            button.setForeground(FG_COLOR);
        }
        
        if (comp instanceof JLabel) {
            comp.setForeground(FG_COLOR);
        }
        
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentColors(child);
            }
        }
    }

    public static JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        
        // Left side - Language Switch
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftPanel.setBackground(BG_COLOR);
        JButton langButton = createStyledButton(isGerman ? "EN" : "DE");
        langButton.addActionListener(e -> {
            isGerman = !isGerman;
            langButton.setText(isGerman ? "EN" : "DE");
            updateAllFrames();
        });
        leftPanel.add(langButton);
        
        // Right side - Dark Mode
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setBackground(BG_COLOR);
        JButton themeButton = createStyledButton(isDarkMode ? "☀️" : "🌙");
        themeButton.addActionListener(e -> {
            toggleTheme();
            themeButton.setText(isDarkMode ? "☀️" : "🌙");
            updateAllFrames();
        });
        rightPanel.add(themeButton);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }

    public static void addHeaderToFrame(JFrame frame) {
        Container contentPane = frame.getContentPane();
        if (contentPane.getLayout() instanceof BorderLayout) {
            contentPane.add(createHeaderPanel(), BorderLayout.NORTH);
        } else {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(BG_COLOR);
            mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
            mainPanel.add((Component) contentPane, BorderLayout.CENTER);
            frame.setContentPane(mainPanel);
        }
        frame.revalidate();
        frame.repaint();
    }

    private static void handleLogout() {
        for (Window window : Window.getWindows()) {
            window.dispose();
        }
        new LoginView().display();
    }
} 