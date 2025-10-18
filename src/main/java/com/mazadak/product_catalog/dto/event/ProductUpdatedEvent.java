package com.mazadak.product_catalog.dto.event;

import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {
    private UUID productId;
    private UUID sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private Long categoryId;
}