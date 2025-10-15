package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.request.CreateCategoryRequestDTO;
import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.mapper.CategoryMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequestDTO createRequest) {
        Category category = categoryMapper.toEntity(createRequest);
        System.out.println(category);
        if (createRequest.getParentId() != null) {
            Category parent = categoryRepository.findById(createRequest.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        System.out.println(savedCategory);
        return categoryMapper.toDTO(savedCategory);
    }


    public List<CategoryDTO> getTopLevelCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CreateCategoryRequestDTO updateRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));


        if (updateRequest.getParentId() != null) {
            if (updateRequest.getParentId().equals(id)) {
                throw new IllegalStateException("A category cannot be its own parent.");
            }
            Category parent = categoryRepository.findById(updateRequest.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        category.setName(updateRequest.getName());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getSubcategories().isEmpty()) {
            throw new IllegalStateException("Cannot delete a category that has subcategories.");
        }

        categoryRepository.delete(category);
    }

    public List<CategoryDTO> getSubcategories(Long parentId) {
        return categoryRepository.findByParentCategory_CategoryId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}