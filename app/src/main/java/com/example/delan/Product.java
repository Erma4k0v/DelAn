package com.example.delan;

import java.io.Serializable;

public class Product implements Serializable {
    private final String name;
    private final String imageUrl;
    private final String description;
    private final double price;

    // Конструкторы, геттеры и сеттеры

    public Product(String name, String imageUrl, String description, double price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

}
