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

    @PostMapping
    public Basket create() {
        return basketService.create();
    }

    @GetMapping("/{id}")
    public Basket getBasket(@PathVariable UUID id) {
        return basketService.getBasket(id);
    }

    @PostMapping("/{id}/add-item")
    public Basket addItem(@PathVariable UUID id,
                          @Valid @RequestBody AddItemDTO addItemDTO) {
        return basketService.addItem(id, addItemDTO);
    }

    @PostMapping("/{id}/add-promotion/{code}")
    public Basket addPromotion(@PathVariable UUID id,
                          @PathVariable("code") String promotionCode) {
        return basketService.addPromotion(id, promotionCode);
    }

    @PatchMapping("/{id}/checkout")
    public Basket checkout(@PathVariable UUID id) {
        return basketService.checkout(id);
    }

}
