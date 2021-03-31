package com.qikserve.supermarket.checkout.services;

import com.qikserve.supermarket.checkout.models.*;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasketService {

    private final List<Basket> baskets;
    private final ProductService productService;
    private final PromotionService promotionService;

    public BasketService(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;

        baskets = new ArrayList<>();
    }

    public Basket create() {
        Basket basket = new Basket();
        baskets.add(basket);
        return basket;
    }

    public Basket getBasket(UUID id) {
        return baskets.stream()
                .filter(b-> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "basket.notfound"));
    }

    public Basket addItem(UUID idBasket, @Valid AddItemDTO addItemDTO) {
        Basket basket = getBasket(idBasket);
        Product product = productService.getProduct(addItemDTO.getIdProduct());
        Item item = getItem(basket, product);
        item.setAmount(item.getAmount() + addItemDTO.getAmount());
        int indexItem = basket.getItems().stream()
                .map(i -> i.getProduct().getId())
                .collect(Collectors.toList())
                .indexOf(addItemDTO.getIdProduct());
        if (indexItem == -1) {
            basket.getItems().add(item);
        }
        return basket;
    }

    private Item getItem(Basket basket, Product product) {
        Item item = basket.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(new Item(product, 0));
        return item;
    }

    public Basket addPromotion(UUID idBasket, String promotionCode) {
        Promotion promotion = promotionService.getPromotion(promotionCode);
        if (promotion.isExpired()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "promotion.expired");
        }

        if (promotionAlreadyExists(idBasket, promotionCode)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "promotion.alreadyexists");
        }

        Basket basket = getBasket(idBasket);
        basket.getPromotions().add(promotion);

        return basket;
    }

    private boolean promotionAlreadyExists(UUID idBasket, String promotionCode) {
        return getBasket(idBasket).getPromotions().stream().anyMatch(p -> p.getCode().equals(promotionCode));
    }
}
