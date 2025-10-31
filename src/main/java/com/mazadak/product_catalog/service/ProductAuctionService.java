package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.ProductAuction;
import com.mazadak.product_catalog.exception.ResourceNotFoundException;
import com.mazadak.product_catalog.repositories.ProductAuctionRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAuctionService {
    private final ProductAuctionRepository productAuctionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void markAuctionAsActive(UUID productId, UUID auctionId) {
        log.info("Marking product active. productId {} auctionId {}", productId, auctionId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "productId", productId.toString());
        }

        var productAuction = productAuctionRepository.findByProductIdAndAuctionId(productId, auctionId)
                        .orElse(new ProductAuction());

        productAuction.setProductId(productId);
        productAuction.setAuctionId(auctionId);
        productAuction.setActive(true);

        productAuctionRepository.save(productAuction);
    }

    @Transactional
    public void markAuctionAsInactive(UUID productId, UUID auctionId) {
        log.info("Marking product inactive. productId {} auctionId {}", productId, auctionId);
        var productAuction = productAuctionRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAuction", "productId, auctionId", productId + " " + auctionId));
        productAuction.setActive(false);
        productAuctionRepository.save(productAuction);
    }

    public boolean isAuctionActive(UUID productId) {
        return productAuctionRepository.findById(productId)
                .map(ProductAuction::isActive)
                .orElse(false);
    }
}