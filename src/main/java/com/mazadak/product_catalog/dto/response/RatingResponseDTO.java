package com.mazadak.product_catalog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingResponseDTO {

    private Long ratingId;
    private Long productId;
    private Long userId;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
}