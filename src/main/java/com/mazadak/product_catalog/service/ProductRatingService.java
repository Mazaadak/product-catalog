package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.entity.ProductRatingDTO;
import com.mazadak.product_catalog.entities.ProductRating;
import com.mazadak.product_catalog.mapper.ProductRatingMapper;
import com.mazadak.product_catalog.repositories.ProductRatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductRatingService {
    private final ProductRatingRepository productRatingRepository;
    private final ProductRatingMapper productRatingMapper;

    public List<ProductRatingDTO> getAllProductRatings() {
        return productRatingMapper.toDTOList(productRatingRepository.findAll());
    }

    public ProductRatingDTO getProductRatingById(Long ratingId) {
        return productRatingMapper.toDTO(productRatingRepository.findById(ratingId).orElse(null));
    }

    public ProductRatingDTO createProductRating(ProductRatingDTO productRatingDTO) {
        ProductRating entity = productRatingMapper.toEntity(productRatingDTO);
        return productRatingMapper.toDTO(productRatingRepository.save(entity));
    }

    public ProductRatingDTO updateProductRating(ProductRatingDTO productRatingDTO) {
        ProductRating existing = productRatingRepository.findById(productRatingDTO.getRatingId()).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setRating(productRatingDTO.getRating());
        existing.setReviewText(productRatingDTO.getReviewText());
        return productRatingMapper.toDTO(productRatingRepository.save(existing));
    }

    public void deleteProductRating(Long ratingId) {
        productRatingRepository.deleteById(ratingId);
    }

    public Page<ProductRatingDTO> getRatingsByProductId(Long productId, Pageable pageable) {
        return productRatingRepository.findByProduct_ProductId(productId, pageable)
                .map(productRatingMapper::toDTO);
    }

    public Page<ProductRatingDTO> getRatingsByUserId(Long userId, Pageable pageable) {
        return productRatingRepository.findByUserId(userId, pageable)
                .map(productRatingMapper::toDTO);
    }
}
