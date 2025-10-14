package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.request.CreateProductRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateProductRequestDTO;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { CategoryMapper.class, ProductRatingMapper.class, ProductImageMapper.class }
)
public interface ProductMapper {
    ProductResponseDTO toDTO(Product product);
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductRequestDTO createRequest);
    void updateEntityFromDto(UpdateProductRequestDTO updateRequest, @MappingTarget Product product);

}