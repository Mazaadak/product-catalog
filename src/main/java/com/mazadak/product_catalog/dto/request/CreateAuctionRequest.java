package com.mazadak.product_catalog.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateAuctionRequest(
        String title,
        BigDecimal startingPrice,
        BigDecimal reservePrice,
        BigDecimal bidIncrement,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
