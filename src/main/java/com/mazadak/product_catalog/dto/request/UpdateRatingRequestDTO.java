package com.mazadak.product_catalog.dto.request;


public record UpdateRatingRequestDTO(
    Integer rating,
    String reviewText
) {}
