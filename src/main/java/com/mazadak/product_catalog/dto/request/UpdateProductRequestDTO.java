package com.mazadak.product_catalog.dto.request;

import com.mazadak.product_catalog.entities.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private ProductStatus status;
}