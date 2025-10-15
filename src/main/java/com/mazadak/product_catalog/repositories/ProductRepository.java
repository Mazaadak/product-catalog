package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    Optional<Product> findProductBySellerIdAndProductId(Long sellerId, Long productId);

}
