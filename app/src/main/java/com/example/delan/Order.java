package com.example.delan;

public class Order {
    private String id;
    private String productName;
    private String customerAddress;
    private String warehouseAddress;
    private String status;

    // Конструкторы, геттеры и сеттеры
    public Order() {}

    public Order(String id, String productName, String customerAddress, String warehouseAddress, String status) {
        this.id = id;
        this.productName = productName;
        this.customerAddress = customerAddress;
        this.warehouseAddress = warehouseAddress;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
