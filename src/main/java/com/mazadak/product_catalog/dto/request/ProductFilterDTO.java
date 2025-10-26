package com.mazadak.product_catalog.dto.request;

import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.entities.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductFilterDTO(
        UUID id,
        UUID sellerId,
        String title,
        String description,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductType type,
        List<Long> categories,
        Integer maxRating
) {
}
