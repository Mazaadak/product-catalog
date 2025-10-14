package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.entity.ProductRatingDTO;
import com.mazadak.product_catalog.entities.ProductRating;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRatingMapper {
    ProductRatingDTO toDTO(ProductRating productRating);
    ProductRating toEntity(ProductRatingDTO productRatingDTO);
    List<ProductRatingDTO> toDTOList(List<ProductRating> productRatings);
    List<ProductRating> toEntityList(List<ProductRatingDTO> productRatingDTOs);
}
