package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.ProductRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    Page<ProductRating> findByProduct_ProductId(Long productId, Pageable pageable);

    Page<ProductRating> findByUserId(Long userId, Pageable pageable);

    boolean existsByProduct_ProductIdAndUserId(Long productId, Long userId);
}
