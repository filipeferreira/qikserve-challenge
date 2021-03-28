package com.qikserve.supermarket.checkout.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddItemDTO {

    @NotNull
    private final UUID idProduct;

    @NotNull
    @Min(1)
    private final Integer amount;
}
