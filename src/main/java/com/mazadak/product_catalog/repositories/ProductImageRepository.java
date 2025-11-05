package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}