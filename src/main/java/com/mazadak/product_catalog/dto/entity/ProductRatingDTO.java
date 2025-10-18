package com.mazadak.product_catalog.dto.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductRatingDTO {
    private Long ratingId;
    private UUID productId;
    private UUID userId;
    private Integer rating;
    private String reviewText;
}
