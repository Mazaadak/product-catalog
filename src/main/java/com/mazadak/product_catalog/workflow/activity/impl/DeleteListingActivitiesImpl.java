package com.mazadak.product_catalog.workflow.activity.impl;

import com.mazadak.product_catalog.client.AuctionClient;
import com.mazadak.product_catalog.client.InventoryClient;
import com.mazadak.product_catalog.dto.client.AuctionResponse;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.service.ProductService;
import com.mazadak.product_catalog.workflow.activity.DeleteListingActivities;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteListingActivitiesImpl implements DeleteListingActivities {
    private final ProductService productService;
    private final InventoryClient inventoryClient;
    private final AuctionClient auctionClient;

    @Override
    public ProductResponseDTO getProduct(UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    public void deleteInventory(UUID productId) {
        inventoryClient.deleteInventory(productId);
    }

    @Override
    public void restoreInventory(UUID productId) {
        inventoryClient.restoreInventory(productId);
    }

    @Override
    public void deleteAuction(UUID auctionId) {
        auctionClient.deleteAuction(auctionId);
    }

    @Override
    public void restoreAuction(UUID auctionId) {
        auctionClient.restoreAuction(auctionId);
    }

    @Override
    public void deleteProduct(UUID productId) {
        productService.deleteProduct(productId);
    }

    @Override
    public AuctionResponse getAuction(UUID productId) {
        return auctionClient.getByProductId(productId);
    }
}
