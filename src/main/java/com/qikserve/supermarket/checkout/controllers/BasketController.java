package com.qikserve.supermarket.checkout.controllers;

import com.qikserve.supermarket.checkout.models.Basket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/baskets")
public class BasketController {


    @GetMapping("/new")
    public Basket create() {
        return new Basket();
    }

}
