package com.mazadak.product_catalog.dto.response;

public record ListingStatusResponse(
        String status,        // "RUNNING", "COMPLETED", "FAILED"
        String listingStatus, // "ACTIVE", "FAILED", "ROLLED_BACK" (only when completed)
        String errorMessage
) {}
