import java.util.*;

public class Main {

    // Main entry point of the program
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        ShopService shopService = new ShopService();
        OrderService orderService = new OrderService();

        System.out.println("Welcome to the Cake Shop!");
        while (true) {
            System.out.println("1. Login\n2. Register\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                User user = userService.login(username, password);
                if (user != null) {
                    if (user instanceof Customer) {
                        shopService.customerInterface((Customer) user, orderService);
                    } else if (user instanceof Employee) {
                        shopService.employeeInterface((Employee) user, orderService, userService);
                    }
                } else {
                    System.out.println("Invalid credentials, try again.");
                }
            } else if (choice == 2) {
                System.out.println("1. Register as Customer\n2. Register as Employee");
                int registerChoice = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (registerChoice == 1) {
                    userService.registerCustomer(username, password);
                } else if (registerChoice == 2) {
                    userService.registerEmployee(username, password);
                } else {
                    System.out.println("Invalid option.");
                }
            } else if (choice == 3) {
                System.out.println("Exiting... Goodbye!");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}

class UserService {
    private final Map<String, User> users = new HashMap<>();

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void registerCustomer(String username, String password) {
        users.put(username, new Customer(username, password));
        System.out.println("Customer registered successfully.");
    }

    public void registerEmployee(String username, String password) {
        users.put(username, new Employee(username, password));
        System.out.println("Employee registered successfully.");
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Employee) {
                employees.add((Employee) user);
            }
        }
        return employees;
    }
}

abstract class User {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Customer extends User {
    private final List<Order> orders = new ArrayList<>();
    private final List<Order> previousOrders = new ArrayList<>();
    private final List<String> shoppingCart = new ArrayList<>();

    public Customer(String username, String password) {
        super(username, password);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Order> getPreviousOrders() {
        return previousOrders;
    }

    public List<String> getShoppingCart() {
        return shoppingCart;
    }
}

class Employee extends User {
    public Employee(String username, String password) {
        super(username, password);
    }
}

class ShopService {
    private final Scanner scanner = new Scanner(System.in);

    public void customerInterface(Customer customer, OrderService orderService) {
        System.out.println("Welcome, " + customer.getUsername() + "!\n");

        while (true) {
            System.out.println("1. Browse Cakes\n2. View Wishlist\n3. View Shopping Cart\n4. My Orders\n5. Previous Orders\n6. Logout");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    orderService.browseCakes(customer);
                    break;
                case 2:
                    orderService.viewWishlist();
                    break;
                case 3:
                    orderService.viewShoppingCart(customer);
                    break;
                case 4:
                    orderService.viewCustomerOrders(customer);
                    break;
                case 5:
                    orderService.viewCustomerPreviousOrders(customer);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public void employeeInterface(Employee employee, OrderService orderService, UserService userService) {
        System.out.println("Welcome, " + employee.getUsername() + "!\n");

        while (true) {
            System.out.println("1. View Orders\n2. Update Order Process\n3. View Stock\n4. Completed Orders\n5. Logout");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    orderService.viewOrders();
                    break;
                case 2:
                    orderService.updateOrderProcess(employee, userService);
                    break;
                case 3:
                    orderService.viewStock();
                    break;
                case 4:
                    orderService.viewCompletedOrders();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}

class Order {
    private static final String[] PROCESSES = {
            "Recipe Being Prepared", "Cake Being Baked", "Cake Being Decorated", "Cake Being Packed", "Cake Ready for Pickup", "Cake Ready for Delivery"};
    private int currentProcess = 0;
    private final String customerName;
    private final String cakeType;
    private boolean isDelivery;
    private boolean assignedDriver = false;
    private boolean notified = false;
    private Customer customer;
    private Employee deliveryDriver;

    public Order(String customerName, String cakeType) {
        this.customerName = customerName;
        this.cakeType = cakeType;
    }

    public void setDeliveryChoice(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public boolean isLastProcess() {
        return currentProcess == PROCESSES.length - 1;
    }

    public boolean isReadyForPickup() {
        return currentProcess == PROCESSES.length - 2;
    }

    public boolean isAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(Employee driver) {
        this.deliveryDriver = driver;
        this.assignedDriver = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void nextProcess() {
        if (currentProcess < PROCESSES.length - 1) {
            currentProcess++;
            System.out.println("Order updated to: " + PROCESSES[currentProcess]);
        } else if (isDelivery && isAssignedDriver()) {
            System.out.println("Mark as delivered? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String delivered = scanner.next();
            if (delivered.equalsIgnoreCase("yes")) {
                System.out.println("Order marked as delivered and moved to completed orders.");
                moveToCompletedOrders();
            }
        } else if (!isDelivery && isReadyForPickup() && !isNotified()) {
            System.out.println("Send message to customer that the order is ready for pickup? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String notify = scanner.next();
            if (notify.equalsIgnoreCase("yes")) {
                setNotified(true);
                System.out.println("Message sent to customer.");
            }
        } else if (!isDelivery && isReadyForPickup() && isNotified()) {
            System.out.println("Mark as picked up? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String pickedUp = scanner.next();
            if (pickedUp.equalsIgnoreCase("yes")) {
                System.out.println("Order marked as picked up and moved to completed orders.");
                moveToCompletedOrders();
            }
        } else {
            System.out.println("Order is already complete.");
        }
    }

    private void moveToCompletedOrders() {
        customer.getOrders().remove(this);
        customer.getPreviousOrders().add(this);
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCakeType() {
        return cakeType;
    }

    @Override
    public String toString() {
        return "Order for " + customerName + " - " + cakeType + ", Current Status: " + PROCESSES[currentProcess];
    }
}

class OrderService {
    private final List<String> cakes = Arrays.asList("Sponge Cake", "Pound Cake", "Cupcake", "Cheesecake", "Fruitcake");
    private final Map<String, Integer> stock = new HashMap<>();
    private final List<String> wishlist = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Order> completedOrders = new ArrayList<>();

    public OrderService() {
        // Initialize stock for ingredients
        stock.put("Flour", 100);
        stock.put("Sugar", 100);
        stock.put("Eggs", 50);
        stock.put("Butter", 50);
        stock.put("Cheese", 20);
    }

    public void browseCakes(Customer customer) {
        System.out.println("Available cakes:");
        for (int i = 0; i < cakes.size(); i++) {
            System.out.println((i + 1) + ". " + cakes.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a cake to add to wishlist or shopping cart (0 to go back):");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= cakes.size()) {
            System.out.println("1. Add to Wishlist\n2. Add to Shopping Cart");
            int action = scanner.nextInt();

            if (action == 1) {
                wishlist.add(cakes.get(choice - 1));
                System.out.println(cakes.get(choice - 1) + " added to wishlist.");
            } else if (action == 2) {
                customer.getShoppingCart().add(cakes.get(choice - 1));
                System.out.println(cakes.get(choice - 1) + " added to shopping cart.");
            }
        }
    }

    public void viewWishlist() {
        System.out.println("Your Wishlist:");
        if (wishlist.isEmpty()) {
            System.out.println("Wishlist is empty.");
        } else {
            wishlist.forEach(System.out::println);
        }
    }

    public void viewShoppingCart(Customer customer) {
        System.out.println("Your Shopping Cart:");
        List<String> shoppingCart = customer.getShoppingCart();
        if (shoppingCart.isEmpty()) {
            System.out.println("Shopping cart is empty.");
            return;
        }
        shoppingCart.forEach(System.out::println);
        System.out.println("Proceed with order?\n1. Yes\n2. No");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 1) {
            placeOrder(customer);
        }
    }

    public void placeOrder(Customer customer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose payment method:\n1. PayPal\n2. Credit Card\n3. Girocard\n4. Bank Transfer\n5. Pay on Invoice");
        int paymentChoice = scanner.nextInt();
        System.out.println("Pick-up or Delivery?\n1. Pick-up\n2. Delivery");
        int deliveryChoice = scanner.nextInt();

        boolean isDelivery = deliveryChoice == 2;

        if (isDelivery) {
            scanner.nextLine(); // consume newline
            System.out.print("Enter delivery address: ");
            String deliveryAddress = scanner.nextLine();
            System.out.print("Is invoice address same as delivery address? (yes/no): ");
            String invoiceChoice = scanner.nextLine();
            if (invoiceChoice.equalsIgnoreCase("no")) {
                System.out.print("Enter invoice address: ");
                scanner.nextLine();
            }
        }

        for (String cake : customer.getShoppingCart()) {
            Order order = new Order(customer.getUsername(), cake);
            order.setDeliveryChoice(isDelivery);
            order.setCustomer(customer); // Properly associate the order with the customer
            customer.getOrders().add(order);
            orders.add(order); // Add to global orders list
        }
        customer.getShoppingCart().clear();
        System.out.println("Order placed successfully! Track it in 'My Orders'.");
    }

    public void viewCustomerOrders(Customer customer) {
        System.out.println("Your Orders:");
        List<Order> customerOrders = customer.getOrders();
        if (customerOrders.isEmpty()) {
            System.out.println("You have no orders.");
        } else {
            customerOrders.forEach(order -> {
                if (order.isDelivery() && order.isAssignedDriver()) {
                    System.out.println("Order for " + order.getCustomerName() + " - " + order.getCakeType() + ", Current Status: Cake is being delivered");
                } else {
                    System.out.println(order);
                }
            });
        }
    }

    public void viewCustomerPreviousOrders(Customer customer) {
        System.out.println("Your Previous Orders:");
        List<Order> previousOrders = customer.getPreviousOrders();
        if (previousOrders.isEmpty()) {
            System.out.println("You have no previous orders.");
        } else {
            previousOrders.forEach(order -> System.out.println(order.getCustomerName() + " - " + order.getCakeType() + ", Current Status: Completed"));
        }
    }

    public void viewOrders() {
        System.out.println("Current Orders:");
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            orders.forEach(order -> {
                if (order.isDelivery() && order.isAssignedDriver()) {
                    System.out.println("Order for " + order.getCustomerName() + " - " + order.getCakeType() + ", Current Status: Cake Ready for Delivery");
                } else {
                    System.out.println(order);
                }
            });
        }
    }

    public void updateOrderProcess(Employee employee, UserService userService) {
        System.out.println("Select an order to update:");
        for (int i = 0; i < orders.size(); i++) {
            System.out.println((i + 1) + ". " + orders.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice > 0 && choice <= orders.size()) {
            Order order = orders.get(choice - 1);

            if (order.isDelivery()) {
                handleDeliveryProcess(order, userService);
            } else {
                handlePickupProcess(order);
            }
        }
    }

    private void handleDeliveryProcess(Order order, UserService userService) {
        Scanner scanner = new Scanner(System.in);

        if (!order.isAssignedDriver()) {
            List<Employee> employees = userService.getAllEmployees();
            System.out.println("Assign a delivery driver:");
            for (int i = 0; i < employees.size(); i++) {
                System.out.println((i + 1) + ". " + employees.get(i).getUsername());
            }
            int driverChoice = scanner.nextInt();
            if (driverChoice > 0 && driverChoice <= employees.size()) {
                order.setAssignedDriver(employees.get(driverChoice - 1));
                System.out.println("Delivery driver " + employees.get(driverChoice - 1).getUsername() + " assigned.");
            }
        } else {
            System.out.println("Mark as delivered? (yes/no)");
            String delivered = scanner.next();
            if (delivered.equalsIgnoreCase("yes")) {
                orders.remove(order);
                completedOrders.add(order);
                order.getCustomer().getPreviousOrders().add(order);
                System.out.println("Order marked as delivered and moved to completed orders.");
            }
        }
    }

    private void handlePickupProcess(Order order) {
        Scanner scanner = new Scanner(System.in);

        if (order.isReadyForPickup()) {
            System.out.println("Send message to customer that the order is ready for pickup? (yes/no)");
            String notify = scanner.next();
            if (notify.equalsIgnoreCase("yes")) {
                order.setNotified(true);
                System.out.println("Message sent to customer.");
            }
        } else if (order.isNotified()) {
            System.out.println("Mark as picked up? (yes/no)");
            String pickedUp = scanner.next();
            if (pickedUp.equalsIgnoreCase("yes")) {
                orders.remove(order);
                completedOrders.add(order);
                order.getCustomer().getPreviousOrders().add(order);
                System.out.println("Order marked as picked up and moved to completed orders.");
            }
        } else {
            order.nextProcess();
        }
    }

    public void viewCompletedOrders() {
        System.out.println("Completed Orders:");
        if (completedOrders.isEmpty()) {
            System.out.println("No completed orders.");
        } else {
            completedOrders.forEach(order -> System.out.println(order.getCustomerName() + " - " + order.getCakeType() + ", Current Status: Completed"));
        }
    }

    public void viewStock() {
        System.out.println("Stock Levels:");
        stock.forEach((ingredient, quantity) -> {
            System.out.println(ingredient + ": " + quantity);
        });
    }
}