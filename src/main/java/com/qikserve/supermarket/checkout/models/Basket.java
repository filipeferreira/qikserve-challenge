package com.qikserve.supermarket.checkout.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Basket {

    private UUID id;
    private List<Item> items;
    private List<Promotion> promotions;

    public Basket() {
        this.id = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.promotions = new ArrayList<>();
    }

}
