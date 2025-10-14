package com.mazadak.product_catalog.dto.entity;

import lombok.Data;

@Data
public class ProductImageDTO {
    private Long imageId;
    private String imageUri;
    private Boolean isPrimary;
    private Integer position;
}
