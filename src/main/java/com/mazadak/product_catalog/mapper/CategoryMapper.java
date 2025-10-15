package com.mazadak.product_catalog.mapper;

import com.mazadak.product_catalog.dto.request.CreateCategoryRequestDTO;
import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CreateCategoryRequestDTO createRequest);

    @Mapping(source = "parentCategory.categoryId", target = "parentId")
    CategoryDTO toDTO(Category category);

    List<CategoryDTO> toDTOList(List<Category> categories);
}
