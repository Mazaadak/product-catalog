package com.mazadak.product_catalog.dto.event;

import java.util.UUID;

public record InventoryDeletedEvent(UUID productId) {
}
