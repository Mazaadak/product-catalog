package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.ProductAuction;
import com.mazadak.product_catalog.repositories.ProductAuctionRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductAuctionService {
    private final ProductAuctionRepository productAuctionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void markAuctionAsActive(Long productId, Long auctionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        ProductAuction productAuction = productAuctionRepository.findById(productId)
                .orElse(new ProductAuction());

        productAuction.setProduct(product);
        productAuction.setAuctionId(auctionId);
        productAuction.setActive(true);

        productAuctionRepository.save(productAuction);
    }

    @Transactional
    public void markAuctionAsInactive(Long productId) {
        productAuctionRepository.findById(productId).ifPresent(productAuction -> {
            productAuction.setActive(false);
            productAuctionRepository.save(productAuction);
        });
    }

    public boolean isAuctionActive(Long productId) {
        return productAuctionRepository.findById(productId)
                .map(ProductAuction::isActive)
                .orElse(false);
    }
}