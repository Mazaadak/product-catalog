package com.mazadak.product_catalog.dto.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuctionStartedEvent(UUID auctionId,
                                  UUID productId,
                                  String title,
                                  LocalDateTime startTime,
                                  Object watchlist) {
}
