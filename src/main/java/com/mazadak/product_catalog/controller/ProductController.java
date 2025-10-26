package com.mazadak.product_catalog.controller;

import com.mazadak.product_catalog.dto.request.*;
import com.mazadak.product_catalog.dto.response.*;
import com.mazadak.product_catalog.service.ProductService;
import com.mazadak.product_catalog.util.IdempotencyUtil;
import com.mazadak.product_catalog.workflow.starter.ListingCreationStarter;
import com.mazadak.product_catalog.workflow.starter.ListingDeletionStarter;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ListingCreationStarter listingCreationStarter;
    private final WorkflowClient workflowClient;
    private final ListingDeletionStarter listingDeletionStarter;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping
    Page<ProductResponseDTO> getProductsByCriteria(
            @ModelAttribute ProductFilterDTO filter,
            Pageable pageable
    ) {
        return productService.getProductsByCriteria(filter, pageable);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-User-Id") UUID currentUserId,
            @RequestBody CreateProductRequestDTO createRequest) {

        String requestHash = IdempotencyUtil.calculateHash(createRequest);
        ProductResponseDTO createdProduct = productService.createProduct(idempotencyKey, requestHash, createRequest, currentUserId);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequestDTO updateRequest,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        ProductResponseDTO updatedProduct = productService.updateProduct(productId, updateRequest, currentUserId);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        productService.assertUserOwnsProduct(currentUserId, productId);
        var workflowId = listingDeletionStarter.startListingDeletion(productId);
        return ResponseEntity.ok(workflowId);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByIds(@RequestBody List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productService.getProductsByIds(productIds));
    }

    @PostMapping("/listings")
    public ResponseEntity<Void> createListing(@RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                              @RequestHeader("X-User-Id") UUID userId,
                                              @RequestBody CreateListingRequest request) {
        if (!request.sellerId().equals(userId)) {
            // TODO: add forbidden exception
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }

        listingCreationStarter.startListingCreation(idempotencyKey, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/listings/{productId}/status")
    public ResponseEntity<ListingStatusResponse> getListingCreationStatus(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId) {

        if (!productService.getProductById(productId).getSellerId().equals(userId)) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }

        String workflowId = "listing-creation-" + productId + "-" + idempotencyKey;

        try {
            WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
            var description = workflowStub.describe();
            var workflowStatus = description.getStatus();

            switch (workflowStatus) {
                case WORKFLOW_EXECUTION_STATUS_COMPLETED:
                    // Get the actual result to determine business success
                    try {
                        WorkflowResult result = workflowStub.getResult(
                                1, TimeUnit.SECONDS, WorkflowResult.class
                        );

                        return ResponseEntity.ok(new ListingStatusResponse(
                                result.isSuccess() ? "COMPLETED" : "FAILED",
                                result.getStatus(),
                                result.getErrorMessage()
                        ));
                    } catch (TimeoutException e) {
                        // Shouldn't happen since workflow is completed
                        return ResponseEntity.ok(new ListingStatusResponse("COMPLETED", null, null));
                    }

                case WORKFLOW_EXECUTION_STATUS_FAILED:
                case WORKFLOW_EXECUTION_STATUS_TERMINATED:
                case WORKFLOW_EXECUTION_STATUS_TIMED_OUT:
                case WORKFLOW_EXECUTION_STATUS_CANCELED:
                    return ResponseEntity.ok(new ListingStatusResponse("FAILED", null, null));

                case WORKFLOW_EXECUTION_STATUS_RUNNING:
                case WORKFLOW_EXECUTION_STATUS_CONTINUED_AS_NEW:
                default:
                    return ResponseEntity.ok(new ListingStatusResponse("RUNNING", null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}