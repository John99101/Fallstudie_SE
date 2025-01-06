package model;

public class Cake {
    private int cakeId;
    private String name;
    private String description;
    private double price;
    private int stockAvailability; // Quantity available in stock
    private int quantity; // Quantity in the order

    // Constructors
    public Cake() {
    }

    public Cake(int cakeId, String name, String description, double price, int stockAvailability) {
        this.cakeId = cakeId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockAvailability = stockAvailability;
    }

    // Getters and Setters
    public int getCakeId() {
        return cakeId;
    }

    public void setCakeId(int cakeId) {
        this.cakeId = cakeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockAvailability() {
        return stockAvailability;
    }

    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability = stockAvailability;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Overriding toString for debugging or display
    @Override
    public String toString() {
        return "Cake{" +
                "cakeId=" + cakeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockAvailability=" + stockAvailability +
                ", quantity=" + quantity +
                '}';
    }
}
