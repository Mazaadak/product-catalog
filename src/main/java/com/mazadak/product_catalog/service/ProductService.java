package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.request.CreateProductRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateProductRequestDTO;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.IdempotencyRecord;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import com.mazadak.product_catalog.repositories.IdempotencyRecordRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ProductAuctionService productAuctionService;

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toDTO);
    }

    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(String idempotencyKey, String requestHash, CreateProductRequestDTO createRequest, Long currentUserId) {
        Optional<IdempotencyRecord> existingRecordOpt = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if (existingRecordOpt.isPresent()) {
            IdempotencyRecord existing = existingRecordOpt.get();
            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
                if (!existing.getRequestHash().equals(requestHash)) {
                    throw new RuntimeException("Idempotency key reused with a different request payload.");
                }
                return productMapper.toDTO(existing.getProduct());
            }
            if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
                throw new RuntimeException("Request with this key is currently in progress. Please retry later.");
            }
        }

        IdempotencyRecord newRecord = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .status(IdempotencyStatus.IN_PROGRESS)
                .build();
        idempotencyRecordRepository.save(newRecord);

        try {
            Product productEntity = productMapper.toEntity(createRequest);
            productEntity.setSellerId(currentUserId);

            Category category = categoryRepository.findById(createRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + createRequest.getCategoryId()));
            productEntity.setCategory(category);

            Product savedProduct = productRepository.save(productEntity);
            newRecord.setProduct(savedProduct);
            newRecord.setStatus(IdempotencyStatus.COMPLETED);
            idempotencyRecordRepository.save(newRecord);
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            newRecord.setStatus(IdempotencyStatus.FAILED);
            idempotencyRecordRepository.save(newRecord);
            throw e;
        }
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, UpdateProductRequestDTO updateRequest, Long currentUserId) {
        if(productAuctionService.isAuctionActive(productId)) {
            throw new IllegalStateException("Cannot edit product while an auction is active.");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existingProduct.getSellerId().equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to edit this product.");
        }

        productMapper.updateEntityFromDto(updateRequest, existingProduct);

        Product savedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long productId, Long currentUserId) {
        if(productAuctionService.isAuctionActive(productId)) {
            throw new IllegalStateException("Cannot edit product while an auction is active.");
        }
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!productToDelete.getSellerId().equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to delete this product.");
        }

        productToDelete.setStatus(ProductStatus.DELETED);
        productRepository.save(productToDelete);
    }

    public ProductResponseDTO getProductBySellerIdAndProductId(Long sellerId, Long productId) {
        Product product = productRepository.findProductBySellerIdAndProductId(sellerId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this seller."));
        return productMapper.toDTO(product);
    }

    public Page<ProductResponseDTO> getProductsBySellerId(Long sellerId, Pageable pageable) {
        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);
        return products.map(productMapper::toDTO);
    }
}