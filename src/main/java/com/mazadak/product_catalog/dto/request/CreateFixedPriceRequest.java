package com.mazadak.product_catalog.dto.request;

import java.math.BigDecimal;

public record CreateFixedPriceRequest(
        BigDecimal price,
        int quantity
) {
}
