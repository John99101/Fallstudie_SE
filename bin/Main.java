import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    private static Connection conn;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Establish database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cakeshop", "root", "password");

            OUTER:
            while (true) {
                System.out.println("\nWelcome to the Cake Shop System!");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> {
                        System.out.println("Goodbye!");
                        break OUTER;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void login() throws SQLException {
        System.out.println("\nEnter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String role = rs.getString("role");
            int userId = rs.getInt("id");
            System.out.println("Login successful!");

            if ("Customer".equalsIgnoreCase(role)) {
                customerMenu(userId);
            } else if ("Employee".equalsIgnoreCase(role)) {
                employeeMenu(userId);
            } else {
                System.out.println("Unknown role. Access denied.");
            }
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private static void register() throws SQLException {
        System.out.println("\nEnter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        System.out.println("Are you registering as a Customer or an Employee?");
        String role = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (username, password, role) VALUES (?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, role);
        stmt.executeUpdate();

        System.out.println("Registration successful!");
    }

    private static void customerMenu(int customerId) throws SQLException {
        OUTER:
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. View Cart");
            System.out.println("2. View Wishlist");
            System.out.println("3. Place Order");
            System.out.println("4. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewCart(customerId);
                case 2 -> viewWishlist(customerId);
                case 3 -> placeOrder(customerId);
                case 4 -> {
                    System.out.println("Logging out...");
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void employeeMenu(int employeeId) throws SQLException {
        OUTER:
        while (true) {
            System.out.println("\nEmployee Menu:");
            System.out.println("1. Update Order Status");
            System.out.println("2. Manage Stock");
            System.out.println("3. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> updateOrderStatus();
                case 2 -> manageStock();
                case 3 -> {
                    System.out.println("Logging out...");
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void viewCart(int customerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT c.type, c.variation, o.quantity FROM Orders o JOIN Cakes c ON o.cake_id = c.id WHERE o.customer_id = ? AND o.status = 'In Cart'"
        );
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
    
        System.out.println("\nYour Cart:");
        boolean hasItems = false;
        while (rs.next()) {
            hasItems = true;
            System.out.println(rs.getString("type") + " - " + rs.getString("variation") + " | Quantity: " + rs.getInt("quantity"));
        }
        if (!hasItems) {
            System.out.println("Your cart is empty.");
        }
    }
    
    private static void viewWishlist(int customerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT c.type, c.variation FROM Orders o JOIN Cakes c ON o.cake_id = c.id WHERE o.customer_id = ? AND o.status = 'In Wishlist'"
        );
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
    
        System.out.println("\nYour Wishlist:");
        boolean hasItems = false;
        while (rs.next()) {
            hasItems = true;
            System.out.println(rs.getString("type") + " - " + rs.getString("variation"));
        }
        if (!hasItems) {
            System.out.println("Your wishlist is empty.");
        }
    }
    
    private static void placeOrder(int customerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT o.id, c.type, c.variation, o.quantity FROM Orders o JOIN Cakes c ON o.cake_id = c.id WHERE o.customer_id = ? AND o.status = 'In Cart'"
        );
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
    
        System.out.println("\nYour Cart for Checkout:");
        boolean hasItems = false;
        while (rs.next()) {
            hasItems = true;
            System.out.println("Order ID: " + rs.getInt("id") + " | " + rs.getString("type") + " - " + rs.getString("variation") + " | Quantity: " + rs.getInt("quantity"));
        }
    
        if (!hasItems) {
            System.out.println("Your cart is empty. Add items to your cart before placing an order.");
            return;
        }
    
        System.out.println("\nChoose Delivery Method:");
        System.out.println("1. Pick-Up\n2. Delivery");
        int choice = scanner.nextInt();
        String deliveryMethod = (choice == 1) ? "Pick-Up" : "Delivery";
    
        PreparedStatement updateStmt = conn.prepareStatement(
            "UPDATE Orders SET status = 'Recipe Being Prepared', delivery_method = ? WHERE customer_id = ? AND status = 'In Cart'"
        );
        updateStmt.setString(1, deliveryMethod);
        updateStmt.setInt(2, customerId);
        updateStmt.executeUpdate();
    
        System.out.println("Order placed successfully!");
    }
    
    private static void updateOrderStatus() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, status FROM Orders WHERE status NOT IN ('Cake Ready')");
    
        System.out.println("\nOrders for Update:");
        while (rs.next()) {
            System.out.println("Order ID: " + rs.getInt("id") + " | Current Status: " + rs.getString("status"));
        }
    
        System.out.println("Enter Order ID to update:");
        int orderId = scanner.nextInt();
    
        System.out.println("Update to:");
        System.out.println("1. Recipe Being Prepared\n2. Cake Being Baked\n3. Cake Being Decorated\n4. Cake Being Packed\n5. Cake Ready");
        int choice = scanner.nextInt();
    
        String[] statuses = {"Recipe Being Prepared", "Cake Being Baked", "Cake Being Decorated", "Cake Being Packed", "Cake Ready"};
        if (choice >= 1 && choice <= 5) {
            PreparedStatement updateStmt = conn.prepareStatement("UPDATE Orders SET status = ? WHERE id = ?");
            updateStmt.setString(1, statuses[choice - 1]);
            updateStmt.setInt(2, orderId);
            updateStmt.executeUpdate();
            System.out.println("Order status updated successfully!");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void manageStock() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Stock");
    
        System.out.println("\nStock Levels:");
        while (rs.next()) {
            System.out.println(rs.getString("material_name") + " | Quantity: " + rs.getInt("quantity") + " | Threshold: " + rs.getInt("threshold"));
            if (rs.getInt("quantity") <= rs.getInt("threshold")) {
                System.out.println("** Low Stock Warning! **");
            }
        }
    
        System.out.println("Enter Material ID to update:");
        int materialId = scanner.nextInt();
        System.out.println("Enter new quantity:");
        int newQuantity = scanner.nextInt();
    
        PreparedStatement updateStmt = conn.prepareStatement("UPDATE Stock SET quantity = ? WHERE id = ?");
        updateStmt.setInt(1, newQuantity);
        updateStmt.setInt(2, materialId);
        updateStmt.executeUpdate();
    
        System.out.println("Stock updated successfully!");
    }
}    