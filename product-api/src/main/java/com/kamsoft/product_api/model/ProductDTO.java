package com.kamsoft.product_api.model;

import java.io.Serializable;
import java.util.UUID;

public class ProductDTO implements Serializable {

    /**
     * needed for Serialization 
     */
    private static final long serialVersionUID = 310260306911082614L;

    private String productIndex; // UUID of the item 
    private String name;
    private String description;
    private String productimage;
    private double price;
    private boolean isActive = true; // all items are by default active
    
    /**
     * 
     */
    public ProductDTO() {
        this.productIndex = UUID.randomUUID().toString(); // auto gen id
        this.price = Double.NaN; // used later to check if value is set to this.
    }

    /**
     * 
     * @param name
     * @param description
     * @param productimage
     * @param price
     */
    public ProductDTO(String name, String description, String productimage, double price) {
        // used to sent data to dynamodb during creation
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.productimage = productimage;
    }

    /**
     * 
     * @param productIndex
     * @param name
     * @param description
     * @param productimage
     * @param price
     */
    public ProductDTO(String productIndex, String name, String description, String productimage, double price) {
        // used to get data from dynamodb
        super();
        this.productIndex = productIndex;
        this.name = name;
        this.description = description;
        this.productimage = productimage;
        this.price = price;
    }

    public String getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(String productIndex) {
        this.productIndex = productIndex;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Produt")
                     .append("Name: ").append(getName());
        return stringBuilder.toString();
    }
    
}
