package com.mazadak.product_catalog.controller;

import com.mazadak.product_catalog.dto.request.CreateCategoryRequestDTO;
import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CreateCategoryRequestDTO createRequest) {
        CategoryDTO newCategory = categoryService.createCategory(createRequest);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long categoryId) {
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long categoryId) {
        List<CategoryDTO> subcategories = categoryService.getSubcategories(categoryId);
        return ResponseEntity.ok(subcategories);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CreateCategoryRequestDTO updateRequest) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, updateRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}