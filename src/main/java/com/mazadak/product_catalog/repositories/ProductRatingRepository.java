package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.ProductRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    Page<ProductRating> findByProduct_ProductId(UUID productId, Pageable pageable);

    Page<ProductRating> findByUserId(UUID userId, Pageable pageable);

    boolean existsByProduct_ProductIdAndUserId(UUID productId, UUID userId);
}
