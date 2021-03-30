package com.qikserve.supermarket.checkout.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {

    @NotNull
    private final Product product;

    @NotNull
    @Min(value = 1)
    private Integer amount;
}
