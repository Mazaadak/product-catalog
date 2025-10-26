package com.mazadak.product_catalog.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListingCreationResult {
    private boolean success;
    private String status; // "ACTIVE", "FAILED", "ROLLED_BACK"
    private String errorMessage;
}