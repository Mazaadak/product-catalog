package com.mazadak.product_catalog.dto;

import com.mazadak.product_catalog.entities.*;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.entities.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private Long sellerId;
    private String title;
    private Category category;
    private String description;
    private BigDecimal price;
    private ProductType type;
    private ProductStatus status;
    private List<ProductImage> images;
    private List<ProductRatingDTO> ratings;

}
