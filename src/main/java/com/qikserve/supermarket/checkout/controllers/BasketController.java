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

    @GetMapping("/{idBasket}")
    public Basket getBasket(@PathVariable UUID idBasket) {
        return basketService.getBasket(idBasket);
    }

    @PostMapping("/{idBasket}/add-item")
    public Basket addItem(@PathVariable UUID idBasket,
                          @Valid @RequestBody AddItemDTO addItemDTO) {
        return basketService.addItem(idBasket, addItemDTO);
    }

    @PostMapping("/{idBasket}/add-promotion/{code}")
    public Basket addPromotion(@PathVariable UUID idBasket,
                          @PathVariable("code") String promotionCode) {
        return basketService.addPromotion(idBasket, promotionCode);
    }

    @PatchMapping("/{idBasket}/checkout")
    public Basket checkout(@PathVariable UUID idBasket) {
        return basketService.checkout(idBasket);
    }

}
