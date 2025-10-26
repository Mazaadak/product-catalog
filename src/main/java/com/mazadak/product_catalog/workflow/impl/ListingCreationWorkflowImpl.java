package com.mazadak.product_catalog.workflow.impl;

import com.mazadak.product_catalog.dto.request.CreateListingRequest;
import com.mazadak.product_catalog.dto.response.ListingCreationResult;
import com.mazadak.product_catalog.entities.enums.ListingStatus;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.workflow.ListingCreationWorkflow;
import com.mazadak.product_catalog.workflow.activity.CreateListingActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

public class ListingCreationWorkflowImpl implements ListingCreationWorkflow {
    private static final Logger log = Workflow.getLogger(ListingCreationWorkflow.class);
    private final CreateListingActivities activities =
            Workflow.newActivityStub(CreateListingActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setMaximumAttempts(3)
                                    .setBackoffCoefficient(2.0)
                                    .setInitialInterval(Duration.ofSeconds(2))
                                    .build()
                            ).build()
            );

    @Override
    public ListingCreationResult createListing(UUID idempotencyKey, CreateListingRequest request) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();

        Saga saga = new Saga(sagaOptions);

        try {
            // STEP 1: validate product exists
            activities.validateProductExists(request.productId());
            log.info("Product {} exists", request.productId());

            // STEP 2: set listing status to CREATING
            activities.setListingStatus(request.productId(), ListingStatus.CREATING);
            saga.addCompensation(() -> activities.setListingStatus(request.productId(), ListingStatus.FAILED));

            // STEP 3: validate product has not associated listing
            activities.validateProductHasNoListing(request.productId());
            log.info("Product {} has no associated listings", request.productId());


            // STEP 4: create listing
            // CASE 1: auction listing
            if (request.type() == ProductType.AUCTION) {
                var auctionId = activities.createAuction(idempotencyKey, request.sellerId(), request.productId(), request.auction());
                saga.addCompensation(() -> activities.deleteAuction(auctionId));
                log.info("An auction with id {} was created for product {}", auctionId, request.productId());
            }

            // CASE 2: fixed-price listing
            if (request.type() == ProductType.FIXED) {
                activities.createInventory(idempotencyKey, request.productId(), request.inventory().quantity());
                saga.addCompensation(() -> activities.deleteInventory(request.productId()));
                log.info("An inventory was created for product {}", request.productId());

                activities.setProductPrice(request.productId(), request.inventory().price());
                saga.addCompensation(() -> activities.setProductPrice(request.productId(), BigDecimal.ZERO));
                log.info("Set product {} price to {}", request.productId(), request.inventory().price());
            }

            // STEP 5: set product type to listing type
            activities.setProductListingType(request.productId(), request.type());
            saga.addCompensation(() -> activities.setProductListingType(request.productId(), ProductType.NONE));
            log.info("Set product {} listing type to {}", request.productId(), request.type());

            // STEP 6: set listing status to ACTIVE
            activities.setListingStatus(request.productId(), ListingStatus.ACTIVE);

            log.info("Product listing created successfully {}", request.productId());
            return new ListingCreationResult(true, "ACTIVE", null);
        } catch (Exception e) {
            log.error("Product creation failed for product {}, {}", request.productId(), e.getMessage());
            saga.compensate();
            return new ListingCreationResult(false, "FAILED", e.getMessage());
        }
    }
}
