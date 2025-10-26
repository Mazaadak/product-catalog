package com.mazadak.product_catalog.dto.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddInventoryRequest (
        @NotNull UUID productId,
        @Positive int quantity
){ }