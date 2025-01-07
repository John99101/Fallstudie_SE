package model;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private String status; // e.g., Processing, Baked, Delivered, etc.
    private String deliveryType; // Delivery or Pick-Up
    private String address; // Required for delivery
    private String paymentMethod; // PayPal, Credit Card, Invoice, etc.
    private double totalPrice;
    private Timestamp orderDate;

    // Constructors
    public Order() {
    }

    public Order(int orderId, int userId, String status, String deliveryType, String address, String paymentMethod, double totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.deliveryType = deliveryType;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    // Overriding toString for debugging or display
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", deliveryType='" + deliveryType + '\'' +
                ", address='" + address + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

