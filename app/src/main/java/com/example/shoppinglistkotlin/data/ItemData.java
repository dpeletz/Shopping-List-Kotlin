package com.example.shoppinglistkotlin.data;

public class ItemData {

    private String category;
    private Integer intCategory;
    private Integer quantity;
    private String name;
    private Float price;
    private String description;
    private Boolean status;

    public ItemData(String category, String name, Float price, String description, Boolean status, Integer intCategory, Integer quantity) {
        this.category = category;
        this.intCategory = intCategory;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getIntCategory() {
        return intCategory;
    }

    public void setIntCategory(Integer intCategory) {
        this.intCategory = intCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}