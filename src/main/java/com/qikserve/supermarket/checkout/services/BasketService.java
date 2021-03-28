package com.qikserve.supermarket.checkout.services;

import com.qikserve.supermarket.checkout.models.Basket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BasketService {

    private final List<Basket> baskets;

    public BasketService() {
        baskets = new ArrayList<>();
    }

    public Basket create() {
        Basket basket = new Basket();
        baskets.add(basket);
        return basket;
    }

    public Basket addItem() {
        return null;
    }
}
