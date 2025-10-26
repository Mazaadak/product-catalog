package com.mazadak.product_catalog.dto.response;

import com.mazadak.product_catalog.dto.entity.*;
import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductResponseDTO {
    private UUID productId;
    private UUID sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductType type;

    private CategoryDTO category;
    private List<ProductImageDTO> images;
    private List<ProductRatingDTO> ratings;
}