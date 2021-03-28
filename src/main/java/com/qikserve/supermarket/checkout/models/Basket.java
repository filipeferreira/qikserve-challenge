package com.qikserve.supermarket.checkout.models;

import lombok.Data;
import lombok.SneakyThrows;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Basket {

    private final UUID id;
    private final List<Item> items;
    private final List<Promotion> promotions;
    private final BasketTotals basketTotals;
    private boolean checkedOut = false;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

    public Basket() {
        this.id = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.promotions = new ArrayList<>();
        this.basketTotals = new BasketTotals();
    }

    @SneakyThrows
    public BasketTotals getBasketTotals() {
        Integer rawTotal = Optional.ofNullable(items.stream().collect(Collectors.summingInt(i -> i.getProduct().getPrice() * i.getAmount()))).orElse(0);
        Integer totalPromos = Optional.ofNullable(promotions.stream().collect(Collectors.summingInt(Promotion::getDiscount))).orElse(0);
        Integer totalPayable = rawTotal - totalPromos;

        if (totalPayable < 0) {
            totalPayable = 0;
        }

        basketTotals.setRawTotal(currencyFormatter.format(rawTotal));
        basketTotals.setTotalPromos(currencyFormatter.format(totalPromos));
        basketTotals.setTotalPayable(currencyFormatter.format(totalPayable));

        return basketTotals;
    }
}
