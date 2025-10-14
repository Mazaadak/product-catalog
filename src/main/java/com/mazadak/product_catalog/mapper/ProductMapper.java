package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.entity.ProductDTO;
import com.mazadak.product_catalog.entities.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO ToDTO(Product product);
    Product ToEntity(ProductDTO productDTO);
    List<ProductDTO> ToDTO(List<Product> products);
    List<Product> ToEntity(List<ProductDTO> productDTOs);
}
