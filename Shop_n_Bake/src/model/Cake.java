package model;

public class Cake {
    private int cakeId;
    private String name;
    private String description;
    private double price;
    private boolean available;
    private int quantity;
    private int stockAvailability;

    // Main constructor
    public Cake(int cakeId, String name, String description, double price, int stockAvailability) {
        this.cakeId = cakeId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockAvailability = stockAvailability;
    }

    // Default constructor
    public Cake() {
        // Empty constructor for when we need to set values later
    }

    // Getters
    public int getCakeId() {
        return cakeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStockAvailability() {
        return stockAvailability;
    }

    // Setters
    public void setCakeId(int cakeId) {
        this.cakeId = cakeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability = stockAvailability;
    }
}

