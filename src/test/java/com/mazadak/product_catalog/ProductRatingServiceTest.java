package com.mazadak.product_catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import com.mazadak.product_catalog.dto.request.CreateRatingRequestDTO;
import com.mazadak.product_catalog.dto.response.RatingResponseDTO;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.ProductRating;
import com.mazadak.product_catalog.mapper.ProductRatingMapper;
import com.mazadak.product_catalog.repositories.OutboxEventRepository;
import com.mazadak.product_catalog.repositories.ProductRatingRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import com.mazadak.product_catalog.repositories.RatingIdempotencyRecordRepository;
import com.mazadak.product_catalog.service.ProductRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRatingServiceTest {

    @Mock
    private ProductRatingRepository productRatingRepository;

    @Mock
    private ProductRatingMapper productRatingMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RatingIdempotencyRecordRepository ratingIdempotencyRecordRepository;

    @Mock
    private OutboxEventRepository outboxRepository;

    @InjectMocks
    private ProductRatingService productRatingService;

    private UUID productId;
    private UUID userId;
    private CreateRatingRequestDTO createRequest;
    private ProductRating productRating;
    private RatingResponseDTO ratingDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();
        createRequest = new CreateRatingRequestDTO();
        createRequest.setRating(5);
        createRequest.setReviewText("Great product!");

        product = new Product();
        product.setProductId(productId);
        product.setSellerId(UUID.randomUUID());

        productRating = new ProductRating();
        productRating.setRatingId(1L);
        productRating.setProduct(product);
        productRating.setUserId(userId);
        productRating.setRating(5);

        ratingDTO = new RatingResponseDTO();
        ratingDTO.setRatingId(1L);
        ratingDTO.setRating(5);
    }

    @Test
    void getAllProductRatings_shouldReturnDTOList() {
        List<ProductRating> ratings = Collections.singletonList(productRating);
        when(productRatingRepository.findAll()).thenReturn(ratings);
        when(productRatingMapper.toDTOList(ratings)).thenReturn(Collections.singletonList(ratingDTO));

        List<RatingResponseDTO> result = productRatingService.getAllProductRatings();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRatingId()).isEqualTo(1L);
        verify(productRatingRepository).findAll();
        verify(productRatingMapper).toDTOList(ratings);
    }

    @Test
    void getProductRatingById_whenFound_shouldReturnDTO() {
        when(productRatingRepository.findById(1L)).thenReturn(Optional.of(productRating));
        when(productRatingMapper.toDTO(productRating)).thenReturn(ratingDTO);

        RatingResponseDTO result = productRatingService.getProductRatingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRatingId()).isEqualTo(1L);
        verify(productRatingRepository).findById(1L);
        verify(productRatingMapper).toDTO(productRating);
    }

    @Test
    void getProductRatingById_whenNotFound_shouldReturnNull() {
        when(productRatingRepository.findById(42L)).thenReturn(Optional.empty());

        RatingResponseDTO result = productRatingService.getProductRatingById(42L);

        assertThat(result).isNull();
        verify(productRatingRepository).findById(42L);
        verifyNoInteractions(productRatingMapper);
    }

    @Test
    void createProductRating_whenProductNotFound_shouldThrowResourceNotFound() {
        when(ratingIdempotencyRecordRepository.findByIdempotencyKey("key")).thenReturn(Optional.empty());

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productRatingService.createProductRating("key", "hash", productId, userId, createRequest)
        );

        verify(productRepository).findById(productId);
        verifyNoInteractions(productRatingRepository);
    }


    @Test
    void getRatingsByProductId_shouldReturnPage() {
        Page<ProductRating> page = new PageImpl<>(Collections.singletonList(productRating));
        when(productRatingRepository.findByProduct_ProductId(productId, Pageable.unpaged())).thenReturn(page);
        when(productRatingMapper.toDTO(productRating)).thenReturn(ratingDTO);

        Page<RatingResponseDTO> result = productRatingService.getRatingsByProductId(productId, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(productRatingRepository).findByProduct_ProductId(productId, Pageable.unpaged());
        verify(productRatingMapper).toDTO(productRating);
    }

    @Test
    void getRatingsByUserId_shouldReturnPage() {
        Page<ProductRating> page = new PageImpl<>(Collections.singletonList(productRating));
        when(productRatingRepository.findByUserId(userId, Pageable.unpaged())).thenReturn(page);
        when(productRatingMapper.toDTO(productRating)).thenReturn(ratingDTO);

        Page<RatingResponseDTO> result = productRatingService.getRatingsByUserId(userId, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(productRatingRepository).findByUserId(userId, Pageable.unpaged());
        verify(productRatingMapper).toDTO(productRating);
    }
}
