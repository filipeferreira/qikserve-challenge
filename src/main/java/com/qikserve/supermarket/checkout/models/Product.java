package com.qikserve.supermarket.checkout.models;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String name;
    /**
     *  Prices are expressed in pennies
     */
    private Integer price;
}
