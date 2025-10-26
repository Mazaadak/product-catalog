package com.mazadak.product_catalog.dto.client;

import java.util.UUID;

public record InventoryDTO(
        UUID productId,
        int totalQuantity,
        int reservedQuantity) {
}
