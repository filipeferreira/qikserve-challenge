package com.qikserve.supermarket.checkout.models;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String name;
    /**
     * Prices are expressed in pennies
     */
    private Integer price;

    public Product id(final String id) {
        this.id = id;
        return this;
    }

    public Product name(final String name) {
        this.name = name;
        return this;
    }

    public Product price(final Integer price) {
        this.price = price;
        return this;
    }
}
