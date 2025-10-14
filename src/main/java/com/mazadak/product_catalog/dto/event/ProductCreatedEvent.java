package com.mazadak.product_catalog.dto.event;

import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private Long productId;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private Long categoryId;

}