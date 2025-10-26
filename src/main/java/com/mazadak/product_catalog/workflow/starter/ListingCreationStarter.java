package com.mazadak.product_catalog.workflow.starter;

import com.mazadak.product_catalog.dto.request.CreateListingRequest;
import com.mazadak.product_catalog.workflow.ListingCreationWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingCreationStarter {
    private final WorkflowClient client;

    public void startListingCreation(UUID idempotencyKey, CreateListingRequest request) {
        String workflowId = "listing-creation-" + request.productId() + "-" + idempotencyKey;

        ListingCreationWorkflow workflow = client.newWorkflowStub(
                ListingCreationWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("LISTING_CREATION_TASK_QUEUE")
                        .setWorkflowId(workflowId)
                        .build()
        );

        WorkflowClient.start(workflow::createListing, idempotencyKey, request);
    }
}
