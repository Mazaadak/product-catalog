package com.mazadak.product_catalog.dto.request;

import lombok.Data;

@Data
public class CreateRatingRequestDTO {
    private int rating;
    private String reviewText;
}
