package com.qikserve.supermarket.checkout.services;

import com.qikserve.supermarket.checkout.models.Promotion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionService {

    private final List<Promotion> promotions;

    public PromotionService() {
        this.promotions = new ArrayList<>();
    }

    public Promotion save(Promotion promotion) {
        if (promotionExists(promotion)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "promotion.alreadyexists");
        }

        promotions.add(promotion);

        return promotion;
    }

    private boolean promotionExists(Promotion promotion) {
        return promotions.stream().anyMatch(p -> p.getId().equals(promotion.getId()) || p.getCode().equals(promotion.getCode()));
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }
}
