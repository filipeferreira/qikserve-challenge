package com.qikserve.supermarket.checkout.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))

public class Item {

    @NotNull
    private final Product product;

    @NotNull
    @Min(value = 1)
    private Integer amount;
}
