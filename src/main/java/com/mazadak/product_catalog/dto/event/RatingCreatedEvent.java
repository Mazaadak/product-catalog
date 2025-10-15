package com.mazadak.product_catalog.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingCreatedEvent {
    private Long ratingId;
    private Long productId;
    private Long sellerId;
    private Long userId;
    private int rating;
}