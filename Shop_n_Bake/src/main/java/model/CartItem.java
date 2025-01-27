package model;

public class CartItem {
    private int cakeId;
    private String name;
    private double price;
    private int quantity;

    public CartItem(int cakeId, String name, double price, int quantity) {
        this.cakeId = cakeId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getCakeId() { return cakeId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 