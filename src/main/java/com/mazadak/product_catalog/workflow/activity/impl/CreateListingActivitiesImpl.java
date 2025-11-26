package com.mazadak.product_catalog.workflow.activity.impl;

import com.mazadak.common.exception.domain.product.ProductListingAlreadyExistsException;
import com.mazadak.product_catalog.client.AuctionClient;
import com.mazadak.product_catalog.client.InventoryClient;
import com.mazadak.product_catalog.client.PaymentClient;
import com.mazadak.product_catalog.dto.client.AddInventoryRequest;
import com.mazadak.product_catalog.dto.request.CreateAuctionRequest;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.service.ProductService;
import com.mazadak.product_catalog.workflow.activity.CreateListingActivities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateListingActivitiesImpl implements CreateListingActivities {
    private final ProductService productService;
    private final AuctionClient auctionClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    @Override
    public void validateProductExists(UUID productId) {
        productService.assertProductExists(productId);
    }

    @Override
    public void validateProductIsNotDeleted(UUID productId) {
        productService.assertProductIsNotDeleted(productId);
    }

    @Override
    public void validateProductHasNoListing(UUID productId) {
        Boolean auctionExists = auctionClient.existsByProductId(productId).getBody();
        Boolean inventoryExists = inventoryClient.existsByProductId(productId).getBody();
        if (auctionExists || inventoryExists) throw new ProductListingAlreadyExistsException("productId (has Auction ? " + auctionExists + " has inventory ? " + inventoryExists + ")");
    }

    @Override
    public String validateSellerHasConnectedStripeAccount(UUID sellerId) {
        return paymentClient.getConnectedAccountId(sellerId).getBody(); // will throw an exception if not found
    }

    @Override
    public void setProductListingType(UUID productId, ProductType type) {
        productService.setProductType(productId, type);
    }

    @Override
    public void setProductPrice(UUID productId, BigDecimal price) {
        productService.setProductPrice(productId, price);
    }

    @Override
    public void createInventory(UUID idempotencyKey, UUID productId, int quantity) {
        inventoryClient.addInventory(idempotencyKey, new AddInventoryRequest(productId, quantity));
    }

    @Override
    public void deleteInventory(UUID productId) {
        inventoryClient.deleteInventory(productId);
    }

    @Override
    public UUID createAuction(UUID idempotencyKey, UUID sellerId, UUID productId, CreateAuctionRequest request) {
        return auctionClient.createAuction(
                idempotencyKey,
                new com.mazadak.product_catalog.dto.client.CreateAuctionRequest(
                        productId,
                        sellerId,
                        request.title(),
                        request.startingPrice(),
                        request.reservePrice(),
                        request.bidIncrement(),
                        request.startTime(),
                        request.endTime()
                )
        ).getBody().id();
    }

    @Override
    public void deleteAuction(UUID auctionId) {
        auctionClient.deleteAuction(auctionId);
    }
}
