package com.mazadak.product_catalog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RatingResponseDTO {

    private Long ratingId;
    private UUID productId;
    private UUID userId;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
}