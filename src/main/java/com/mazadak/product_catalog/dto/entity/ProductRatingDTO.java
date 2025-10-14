package com.mazadak.product_catalog.dto.entity;

import lombok.Data;

@Data
public class ProductRatingDTO {
    private Long ratingId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String reviewText;
}
