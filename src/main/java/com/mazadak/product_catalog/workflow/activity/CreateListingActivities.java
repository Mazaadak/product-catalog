package com.mazadak.product_catalog.workflow.activity;

import com.mazadak.product_catalog.dto.request.CreateAuctionRequest;
import com.mazadak.product_catalog.entities.enums.ProductType;
import io.temporal.activity.ActivityInterface;

import java.math.BigDecimal;
import java.util.UUID;

@ActivityInterface
public interface CreateListingActivities {
    void validateProductExists(UUID productId);
    void validateProductIsNotDeleted(UUID productId);
    void validateProductHasNoListing(UUID productId);
    void setProductListingType(UUID productId, ProductType type);
    void setProductPrice(UUID productId, BigDecimal price);
    void createInventory(UUID idempotencyKey, UUID productId, int quantity);
    void deleteInventory(UUID productId);
    UUID createAuction(UUID idempotencyKey, UUID sellerId, UUID productId, CreateAuctionRequest request);
    void deleteAuction(UUID auctionId);
}
