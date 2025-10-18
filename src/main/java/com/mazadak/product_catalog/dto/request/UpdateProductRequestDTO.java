package com.mazadak.product_catalog.dto.request;

import com.mazadak.product_catalog.entities.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDTO {
    private UUID productId;
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private ProductStatus status;
}