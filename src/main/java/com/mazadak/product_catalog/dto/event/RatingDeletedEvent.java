package com.mazadak.product_catalog.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDeletedEvent {
    private Long ratingId;
    private UUID productId;
    private UUID userId;
}
