package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.ProductAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAuctionRepository extends JpaRepository<ProductAuction, Long> {

}
