package com.mazadak.product_catalog.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RatingUpdatedEvent {
    private Long ratingId;
    private UUID productId;
    private UUID userId;
    private Integer rating;
    private String reviewText;
}
