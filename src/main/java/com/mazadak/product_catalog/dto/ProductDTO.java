package com.mazadak.product_catalog.dto;

import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.ProductImage;
import com.mazadak.product_catalog.entities.ProductRating;
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
    private String type;
    private String status;
    private List<ProductImage> productImages;
    private List<ProductRating> productRatings;

}
