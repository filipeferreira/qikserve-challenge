package com.qikserve.supermarket.checkout.models;

import lombok.Data;

@Data
public class BasketTotals {

    private String rawTotal;
    private String totalPromos;
    private String totalPayable;
}
