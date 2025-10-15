package com.mazadak.product_catalog.dto.response;

import lombok.Data;


@Data
public class CategoryDTO {
    private Long categoryId;
    private String name;
    private Long parentId;
}
