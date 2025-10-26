package com.mazadak.product_catalog.workflow.impl;

import com.mazadak.product_catalog.dto.response.WorkflowResult;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.workflow.ListingCreationWorkflow;
import com.mazadak.product_catalog.workflow.ListingDeletionWorkflow;
import com.mazadak.product_catalog.workflow.activity.CreateListingActivities;
import com.mazadak.product_catalog.workflow.activity.DeleteListingActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.UUID;

public class ListingDeletionWorkflowImpl implements ListingDeletionWorkflow {
    private static final Logger log = Workflow.getLogger(ListingDeletionWorkflow.class);
    private final DeleteListingActivities activities =
            Workflow.newActivityStub(DeleteListingActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setMaximumAttempts(3)
                                    .setBackoffCoefficient(2.0)
                                    .setInitialInterval(Duration.ofSeconds(2))
                                    .build()
                            ).build()
            );

    private UUID auctionId;

    @Override
    public WorkflowResult deleteListing(UUID productId) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();

        Saga saga = new Saga(sagaOptions);

        try {
            // STEP 1: get product
            var product = activities.getProduct(productId);
            log.info("Fetched product details for product {}", productId);

            // STEP 2: delete associated listings if any
            // CASE 1: auction listing
            if (product.getType() == ProductType.AUCTION) {
                var auction = activities.getAuction(productId);
                auctionId = auction.id();

                activities.deleteAuction(auctionId);
                saga.addCompensation(() -> activities.restoreAuction(auctionId));

                log.info("Deleted auction {} for product {}", auctionId, productId);
            }

            // CASE 2: fixed price listing
            if (product.getType() == ProductType.FIXED) {
                activities.deleteInventory(productId);
                saga.addCompensation(() -> activities.restoreInventory(productId));
                log.info("Deleted inventory for product {}", productId);
            }

            // STEP 3: delete product
            activities.deleteProduct(productId);
            log.info("Product deletion successful for product {}", productId);

            return new WorkflowResult(true, "ACTIVE", null);
        } catch (Exception ex) {
            log.error("Product deletion failed for product {}, {}", productId, ex.getMessage());
            saga.compensate();
            return new WorkflowResult(false, "FAILED", ex.getMessage());
        }
    }
}
