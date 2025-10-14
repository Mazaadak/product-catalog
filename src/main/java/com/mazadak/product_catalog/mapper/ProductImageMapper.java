package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.entity.ProductImageDTO;
import com.mazadak.product_catalog.entities.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDTO toDTO(ProductImage productImage);
    ProductImage toEntity(ProductImageDTO productImageDTO);
    List<ProductImageDTO> toDTOList(List<ProductImage> productImages);
    List<ProductImage> toEntityList(List<ProductImageDTO> productImageDTOs);
}
