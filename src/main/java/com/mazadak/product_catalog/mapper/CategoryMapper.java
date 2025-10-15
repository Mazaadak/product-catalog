package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.entity.CategoryDTO;
import com.mazadak.product_catalog.entities.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    Category toEntity(CategoryDTO categoryDTO);
    List<CategoryDTO> toDTOList(List<Category> categories);
    List<Category> toEntityList(List<CategoryDTO> categoryDTOs);
}
