package com.example.delan;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;  // Новый идентификатор продукта
    private String name;
    private String imageUrl;
    private String description;
    private double price;
    private String supplierId;  // Новый идентификатор поставщика

    // Пустой конструктор необходим для Firestore
    public Product() {}

    public Product(String id, String name, String imageUrl, String description, double price, String supplierId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.supplierId = supplierId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}