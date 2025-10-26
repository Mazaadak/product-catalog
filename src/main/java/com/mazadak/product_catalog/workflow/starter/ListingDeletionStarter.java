package com.mazadak.product_catalog.workflow.starter;

import com.mazadak.product_catalog.dto.request.CreateListingRequest;
import com.mazadak.product_catalog.workflow.ListingCreationWorkflow;
import com.mazadak.product_catalog.workflow.ListingDeletionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingDeletionStarter {
    private final WorkflowClient client;

    public String startListingDeletion(UUID productId) {
        String workflowId = "listing-deletion-" + productId + "-" + UUID.randomUUID();

        ListingDeletionWorkflow workflow = client.newWorkflowStub(
                ListingDeletionWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("LISTING_DELETION_TASK_QUEUE")
                        .setWorkflowId(workflowId)
                        .build()
        );

        WorkflowClient.start(workflow::deleteListing, productId);
        return workflowId;
    }
}