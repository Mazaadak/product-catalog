package com.mazadak.product_catalog.workflow;

import com.mazadak.product_catalog.dto.request.CreateListingRequest;
import com.mazadak.product_catalog.dto.response.ListingCreationResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface ListingCreationWorkflow {
    @WorkflowMethod
    ListingCreationResult createListing(UUID idempotencyKey, CreateListingRequest request);
}
