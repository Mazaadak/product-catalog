package com.mazadak.product_catalog;

import com.mazadak.product_catalog.dto.request.CreateCategoryRequestDTO;
import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import com.mazadak.product_catalog.mapper.CategoryMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import com.mazadak.product_catalog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private CreateCategoryRequestDTO createRequest;
    private Category categoryEntity;
    private Category savedCategoryEntity;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        createRequest = new CreateCategoryRequestDTO();
        createRequest.setName("Electronics");

        categoryEntity = new Category();
        categoryEntity.setName("Electronics");

        savedCategoryEntity = new Category();
        savedCategoryEntity.setCategoryId(1L);
        savedCategoryEntity.setName("Electronics");

        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setName("Electronics");
    }

    @Test
    void createCategory_should_map_save_and_returnDto() {
        when(categoryMapper.toEntity(createRequest)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(savedCategoryEntity);
        when(categoryMapper.toDTO(savedCategoryEntity)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");

        verify(categoryMapper).toEntity(createRequest);
        verify(categoryRepository).save(categoryEntity);
        verify(categoryMapper).toDTO(savedCategoryEntity);
    }

    @Test
    void getCategoryById_whenFound_shouldReturnDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(savedCategoryEntity));
        when(categoryMapper.toDTO(savedCategoryEntity)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");

        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDTO(savedCategoryEntity);
    }

    @Test
    void getCategoryById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(categoryRepository.findById(42L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(42L));

        assertThat(ex).hasMessage("Category not found");

        verify(categoryRepository).findById(42L);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void updateCategory_whenFound_shouldUpdateAndReturnDto() {
        CreateCategoryRequestDTO updateRequest = new CreateCategoryRequestDTO();
        updateRequest.setName("Home Electronics");

        Category existing = new Category();
        existing.setCategoryId(1L);
        existing.setName("Electronics");

        Category updatedEntity = new Category();
        updatedEntity.setCategoryId(1L);
        updatedEntity.setName("Home Electronics");

        CategoryDTO updatedDto = new CategoryDTO();
        updatedDto.setCategoryId(1L);
        updatedDto.setName("Home Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updatedEntity);
        when(categoryMapper.toDTO(updatedEntity)).thenReturn(updatedDto);

        CategoryDTO result = categoryService.updateCategory(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Home Electronics");
        assertThat(result.getCategoryId()).isEqualTo(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existing);
        verify(categoryMapper).toDTO(updatedEntity);
    }

    @Test
    void updateCategory_whenNotFound_shouldThrowResourceNotFoundException() {
        CreateCategoryRequestDTO updateRequest = new CreateCategoryRequestDTO();
        updateRequest.setName("Any");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, updateRequest));

        assertThat(ex).hasMessage("Category not found");

        verify(categoryRepository).findById(99L);
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void deleteCategory_whenFound_shouldDelete() {
        Category existing = new Category();
        existing.setCategoryId(1L);
        existing.setName("Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(existing);
    }

    @Test
    void deleteCategory_whenNotFound_shouldThrowResourceNotFoundException() {
        when(categoryRepository.findById(55L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(55L));

        assertThat(ex).hasMessage("Category not found");

        verify(categoryRepository).findById(55L);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void getCategories_should_returnDtoList() {
        Category c1 = new Category();
        c1.setCategoryId(1L);
        c1.setName("A");

        Category c2 = new Category();
        c2.setCategoryId(2L);
        c2.setName("B");

        CategoryDTO d1 = new CategoryDTO();
        d1.setCategoryId(1L);
        d1.setName("A");

        CategoryDTO d2 = new CategoryDTO();
        d2.setCategoryId(2L);
        d2.setName("B");

        List<Category> entities = Arrays.asList(c1, c2);
        List<CategoryDTO> dtos = Arrays.asList(d1, d2);

        when(categoryRepository.findAll()).thenReturn(entities);
        when(categoryMapper.toDTOList(entities)).thenReturn(dtos);

        List<CategoryDTO> result = categoryService.getCategories();

        assertThat(result).hasSize(2)
                .extracting(CategoryDTO::getName)
                .containsExactlyInAnyOrder("A", "B");

        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTOList(entities);
    }
}
