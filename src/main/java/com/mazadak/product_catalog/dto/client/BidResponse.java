package com.mazadak.product_catalog.dto.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record BidResponse(
        UUID id,
        UUID auctionId,
        UUID bidderId,
        BigDecimal amount,
        String idempotencyKey // TODO: remove
) implements Serializable { }
