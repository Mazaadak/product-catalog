package com.mazadak.product_catalog.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRatingDTO {
    private Long ratingId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String reviewText;
}
