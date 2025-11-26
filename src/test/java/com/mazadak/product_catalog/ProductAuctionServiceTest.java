package com.mazadak.product_catalog;

import com.mazadak.common.exception.shared.ResourceNotFoundException;
import com.mazadak.product_catalog.entities.ProductAuction;
import com.mazadak.product_catalog.repositories.ProductAuctionRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import com.mazadak.product_catalog.service.ProductAuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAuctionServiceTest {

    @Mock
    private ProductAuctionRepository productAuctionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductAuctionService productAuctionService;

    private UUID productId;
    private UUID auctionId;
    private ProductAuction productAuction;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        auctionId = UUID.randomUUID();

        productAuction = new ProductAuction();
        productAuction.setProductId(productId);
        productAuction.setAuctionId(auctionId);
        productAuction.setActive(false);
    }

    @Test
    void markAuctionAsActive_whenProductExistsAndAuctionExists_shouldActivateAuction() {
        when(productRepository.existsById(productId)).thenReturn(true);
        when(productAuctionRepository.findByProductIdAndAuctionId(productId, auctionId))
                .thenReturn(Optional.of(productAuction));
        when(productAuctionRepository.save(any(ProductAuction.class))).thenReturn(productAuction);

        productAuctionService.markAuctionAsActive(productId, auctionId);

        assertThat(productAuction.isActive()).isTrue();
        verify(productRepository).existsById(productId);
        verify(productAuctionRepository).findByProductIdAndAuctionId(productId, auctionId);
        verify(productAuctionRepository).save(productAuction);
    }

    @Test
    void markAuctionAsActive_whenProductDoesNotExist_shouldThrowResourceNotFoundException() {
        when(productRepository.existsById(productId)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productAuctionService.markAuctionAsActive(productId, auctionId));

        assertThat(ex.getMessage()).contains("Product");
        verify(productRepository).existsById(productId);
        verifyNoInteractions(productAuctionRepository);
    }

    @Test
    void markAuctionAsInactive_whenAuctionExists_shouldDeactivateAuction() {
        when(productAuctionRepository.findById(productId)).thenReturn(Optional.of(productAuction));
        when(productAuctionRepository.save(any(ProductAuction.class))).thenReturn(productAuction);

        productAuctionService.markAuctionAsInactive(productId, auctionId);

        assertThat(productAuction.isActive()).isFalse();
        verify(productAuctionRepository).findById(productId);
        verify(productAuctionRepository).save(productAuction);
    }

    @Test
    void markAuctionAsInactive_whenAuctionDoesNotExist_shouldThrowResourceNotFoundException() {
        when(productAuctionRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productAuctionService.markAuctionAsInactive(productId, auctionId));

        assertThat(ex.getMessage()).contains("ProductAuction");
        verify(productAuctionRepository).findById(productId);
        verify(productAuctionRepository, never()).save(any());
    }

    @Test
    void isAuctionActive_whenAuctionExists_shouldReturnTrue() {
        productAuction.setActive(true);
        when(productAuctionRepository.findById(productId)).thenReturn(Optional.of(productAuction));

        boolean result = productAuctionService.isAuctionActive(productId);

        assertThat(result).isTrue();
        verify(productAuctionRepository).findById(productId);
    }

    @Test
    void isAuctionActive_whenAuctionDoesNotExist_shouldReturnFalse() {
        when(productAuctionRepository.findById(productId)).thenReturn(Optional.empty());

        boolean result = productAuctionService.isAuctionActive(productId);

        assertThat(result).isFalse();
        verify(productAuctionRepository).findById(productId);
    }
}
