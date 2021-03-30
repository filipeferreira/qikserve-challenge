package com.qikserve.supermarket.checkout.controllers;

import com.qikserve.supermarket.checkout.models.AddItemDTO;
import com.qikserve.supermarket.checkout.models.Basket;
import com.qikserve.supermarket.checkout.services.BasketService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/new")
    public Basket create() {
        return basketService.create();
    }

    @PostMapping("/{idBasket}/add-item")
    public Basket addItem(@PathVariable UUID idBasket,
                          @Valid @RequestBody AddItemDTO addItemDTO) {
        return basketService.addItem(idBasket, addItemDTO);
    }

}
