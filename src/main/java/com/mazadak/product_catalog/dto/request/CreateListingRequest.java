package com.mazadak.product_catalog.dto.request;

import com.mazadak.product_catalog.entities.enums.ProductType;

import java.util.UUID;

public record CreateListingRequest(
        UUID productId,
        UUID sellerId,
        ProductType type,
        CreateAuctionRequest auction,
        CreateFixedPriceRequest inventory
) {
}
