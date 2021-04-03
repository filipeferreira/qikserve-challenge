package com.qikserve.supermarket.checkout.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Promotion {

    private final UUID id;
    private final String code;
    /**
     * Discount expressed in pennies
     */
    private final Integer discount;

    private final LocalDate expiration;

    public Promotion(String code, Integer discount, LocalDate expiration) {
        this.id = UUID.randomUUID();
        this.code = code.toUpperCase();
        this.discount = discount;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiration);
    }
}
