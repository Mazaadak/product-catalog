package com.mazadak.product_catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import com.mazadak.product_catalog.dto.request.CreateProductRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateProductRequestDTO;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.*;
import com.mazadak.product_catalog.service.ImageUploadService;
import com.mazadak.product_catalog.service.ProductAuctionService;
import com.mazadak.product_catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductMapper productMapper;
    @Mock private IdempotencyRecordRepository idempotencyRecordRepository;
    @Mock private ProductAuctionService productAuctionService;
    @Mock private OutboxEventRepository outboxRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ImageUploadService imageUploadService;

    @InjectMocks private ProductService productService;

    private UUID productId;
    private UUID sellerId;
    private Product productEntity;
    private ProductResponseDTO productDTO;
    private CreateProductRequestDTO createRequest;
    private UpdateProductRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sellerId = UUID.randomUUID();

        createRequest = new CreateProductRequestDTO();
        createRequest.setTitle("Phone");
        createRequest.setDescription("Smartphone");
        createRequest.setPrice(BigDecimal.valueOf(1000));
        createRequest.setCategoryId(1L);

        productEntity = new Product();
        productEntity.setProductId(productId);
        productEntity.setSellerId(sellerId);
        productEntity.setTitle("Phone");
        productEntity.setDescription("Smartphone");
        productEntity.setPrice(BigDecimal.valueOf(1000));
        productEntity.setCategory(new Category());

        productDTO = new ProductResponseDTO();
        productDTO.setProductId(productId);
        productDTO.setTitle("Phone");
    }

    @Test
    void getProductById_whenProductExists_shouldReturnDTO() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productMapper.toDTO(productEntity)).thenReturn(productDTO);

        ProductResponseDTO result = productService.getProductById(productId);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        verify(productRepository).findById(productId);
        verify(productMapper).toDTO(productEntity);
    }

    @Test
    void getProductById_whenProductNotFound_shouldThrowResourceNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
    }

    @Test
    void getAllProducts_shouldReturnPageOfDTOs() {
        Page<Product> page = new PageImpl<>(List.of(productEntity));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(productMapper.toDTO(productEntity)).thenReturn(productDTO);

        Page<ProductResponseDTO> result = productService.getAllProducts(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductId()).isEqualTo(productId);
        verify(productRepository).findAll(any(Pageable.class));
        verify(productMapper).toDTO(productEntity);
    }

    @Test
    void createProduct_whenCategoryNotFound_shouldThrowResourceNotFound() throws Exception {
        when(idempotencyRecordRepository.findByIdempotencyKey("key")).thenReturn(Optional.empty());
        when(idempotencyRecordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Product productEntity = new Product();
        when(productMapper.toEntity(createRequest)).thenReturn(productEntity);

        assertThrows(ResourceNotFoundException.class, () ->
                productService.createProduct("key", "hash", createRequest, sellerId, null)
        );

        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }



    @Test
    void updateProduct_whenProductNotFound_shouldThrowResourceNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(productId, updateRequest, sellerId)
        );
        verify(productRepository).findById(productId);
    }

    @Test
    void setProductType_whenProductNotFound_shouldThrowResourceNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.setProductType(productId, ProductType.FIXED)
        );
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductsByIds_whenSomeProductsMissing_shouldThrowResourceNotFound() {
        UUID missingId = UUID.randomUUID();
        when(productRepository.findAllById(List.of(productId, missingId)))
                .thenReturn(List.of(productEntity));

        assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductsByIds(List.of(productId, missingId))
        );
        verify(productRepository).findAllById(List.of(productId, missingId));
    }
}
