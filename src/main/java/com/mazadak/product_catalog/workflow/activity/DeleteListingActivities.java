package com.mazadak.product_catalog.workflow.activity;

import com.mazadak.product_catalog.dto.client.AuctionResponse;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.entities.Product;
import io.temporal.activity.ActivityInterface;

import java.util.UUID;

@ActivityInterface
public interface DeleteListingActivities {
    ProductResponseDTO getProduct(UUID productId);

    void deleteInventory(UUID productId);
    void restoreInventory(UUID productId);

    void deleteAuction(UUID auctionId);
    void restoreAuction(UUID auctionId);

    void deleteProduct(UUID productId);

    AuctionResponse getAuction(UUID productId);
}

