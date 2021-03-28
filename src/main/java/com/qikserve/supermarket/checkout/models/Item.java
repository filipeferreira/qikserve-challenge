package com.qikserve.supermarket.checkout.models;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Item {

    @NotNull
    private Product product;

    @NotNull
    @Min(value = 1)
    private Integer amount;
}
