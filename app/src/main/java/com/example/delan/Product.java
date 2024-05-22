package com.example.delan;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String imageUrl;
    private String description;
    private double price;

    // Пустой конструктор необходим для Firestore
    public Product() {}

    public Product(String name, String imageUrl, String description, double price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
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
}
