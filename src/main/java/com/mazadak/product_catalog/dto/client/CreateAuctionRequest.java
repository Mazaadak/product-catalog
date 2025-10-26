package com.mazadak.product_catalog.dto.client;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAuctionRequest(
        UUID productId,
        UUID sellerId,
        String title,
        BigDecimal startingPrice,
        BigDecimal reservePrice,
        BigDecimal bidIncrement,
        LocalDateTime startTime,
        LocalDateTime endTime)
        implements Serializable {
}
