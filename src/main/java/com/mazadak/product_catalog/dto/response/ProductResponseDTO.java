package com.mazadak.product_catalog.dto.response;

import com.mazadak.product_catalog.dto.entity.*;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long productId;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private ProductStatus status;

    private CategoryDTO category;
    private List<ProductImageDTO> images;
    private List<ProductRatingDTO> ratings;
}