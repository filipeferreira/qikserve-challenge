package com.qikserve.supermarket.checkout.controllers;

import com.qikserve.supermarket.checkout.models.Promotion;
import com.qikserve.supermarket.checkout.services.PromotionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping
    public Promotion save(@RequestBody Promotion promotion) {
        return promotionService.save(promotion);
    }

    @GetMapping
    public List<Promotion> getPromotions() {
        return promotionService.getPromotions();
    }
}
