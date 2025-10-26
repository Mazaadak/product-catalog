package com.mazadak.product_catalog.workflow;

import com.mazadak.product_catalog.dto.response.WorkflowResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface ListingDeletionWorkflow {
    @WorkflowMethod
    WorkflowResult deleteListing(UUID productId);
}
