package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.repositories.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findBySellerId(UUID sellerId, Pageable pageable);
    Optional<Product> findProductBySellerIdAndProductId(UUID sellerId, UUID productId);
}
