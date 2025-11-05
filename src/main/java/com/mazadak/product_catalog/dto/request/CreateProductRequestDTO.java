package com.mazadak.product_catalog.dto.request;

import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private Long categoryId;
}