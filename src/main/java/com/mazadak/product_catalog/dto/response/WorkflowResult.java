package com.mazadak.product_catalog.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkflowResult {
    private boolean success;
    private String status; // "ACTIVE", "FAILED", "ROLLED_BACK"
    private String errorMessage;
}