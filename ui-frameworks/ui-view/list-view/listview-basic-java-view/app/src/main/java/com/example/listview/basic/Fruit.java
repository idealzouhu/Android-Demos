package com.example.listview.basic;

public class Fruit {
    private String name;
    private int imageId;
    private String description;
    private double price;

    public Fruit(String name, int imageId, String description, double price) {
        this.name = name;
        this.imageId = imageId;
        this.description = description;
        this.price = price;
    }

    // Getter 方法
    public String getName() { return name; }
    public int getImageId() { return imageId; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }

    // Setter 方法
    public void setName(String name) { this.name = name; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
}
