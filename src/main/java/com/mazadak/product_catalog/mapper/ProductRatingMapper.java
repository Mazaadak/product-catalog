package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.request.CreateRatingRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateRatingRequestDTO;
import com.mazadak.product_catalog.dto.response.RatingResponseDTO;
import com.mazadak.product_catalog.entities.ProductRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRatingMapper {

    RatingResponseDTO toDTO(ProductRating productRating);

    ProductRating toEntity(CreateRatingRequestDTO createRequest);

    List<RatingResponseDTO> toDTOList(List<ProductRating> productRatings);

    void updateEntityFromDto(UpdateRatingRequestDTO updateRequest, @MappingTarget ProductRating productRating);
}