package com.qikserve.supermarket.checkout.models;

import lombok.Data;

@Data
public class Item {

    private Product product;
    private Integer amount;
    /**
     * prices are expressed in pennies
     */
    private Integer price;
}
