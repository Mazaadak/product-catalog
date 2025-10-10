package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.ProductRatingDTO;
import com.mazadak.product_catalog.entities.ProductRating;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRatingMapper {
    ProductRatingDTO ToDTO(ProductRating productRating);
    ProductRating ToEntity(ProductRatingDTO productRatingDTO);
    List<ProductRatingDTO> ToDTOList(List<ProductRating> productRatings);
    List<ProductRating> ToEntityList(List<ProductRatingDTO> productRatingDTOs);
}
