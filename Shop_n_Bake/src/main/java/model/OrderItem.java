package model;

public class OrderItem {
    private Cake cake;
    private int quantity;

    public OrderItem(Cake cake, int quantity) {
        this.cake = cake;
        this.quantity = quantity;
    }

    public Cake getCake() {
        return cake;
    }

    public int getQuantity() {
        return quantity;
    }
} 