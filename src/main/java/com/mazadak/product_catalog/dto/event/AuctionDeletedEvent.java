package com.mazadak.product_catalog.dto.event;

import java.util.UUID;

public record AuctionDeletedEvent(UUID auctionId, UUID productId) {
}