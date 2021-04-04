package com.qikserve.supermarket.checkout.models;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class Promotion {

    private final UUID id;

    @NotBlank
    private final String code;
    /**
     * Discount expressed in pennies
     */
    @NotNull
    @Min(1)
    private final Integer discount;

    @NotNull
    private final LocalDate expiration;

    public Promotion(String code, Integer discount, LocalDate expiration) {
        this.id = UUID.randomUUID();
        this.code = code == null ? null : code.toUpperCase();
        this.discount = discount;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return expiration == null || LocalDate.now().isAfter(expiration);
    }
}
