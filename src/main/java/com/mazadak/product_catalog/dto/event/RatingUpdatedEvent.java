package com.mazadak.product_catalog.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RatingUpdatedEvent {
    private Long ratingId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String reviewText;
}
