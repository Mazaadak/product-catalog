package com.mazadak.product_catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.product_catalog.dto.event.ProductCreatedEvent;
import com.mazadak.product_catalog.dto.event.ProductDeletedEvent;
import com.mazadak.product_catalog.dto.event.ProductUpdatedEvent;
import com.mazadak.product_catalog.dto.request.CreateProductRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateProductRequestDTO;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.IdempotencyRecord;
import com.mazadak.product_catalog.entities.OutboxEvent;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import com.mazadak.product_catalog.repositories.IdempotencyRecordRepository;
import com.mazadak.product_catalog.repositories.OutboxEventRepository;
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
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

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
        Optional<ProductResponseDTO> existingResponse = handleIdempotencyCheck(idempotencyKey, requestHash);
        if (existingResponse.isPresent()) {
            return existingResponse.get();
        }

        IdempotencyRecord newRecord = idempotencyRecordRepository.save(
                IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .requestHash(requestHash)
                        .status(IdempotencyStatus.IN_PROGRESS)
                        .build()
        );

        try {
            Product productEntity = productMapper.toEntity(createRequest);
            productEntity.setSellerId(currentUserId);

            Category category = categoryRepository.findById(createRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + createRequest.getCategoryId()));
            productEntity.setCategory(category);

            Product savedProduct = productRepository.save(productEntity);
            createProductOutboxEvent(savedProduct);

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

    private Optional<ProductResponseDTO> handleIdempotencyCheck(String idempotencyKey, String requestHash) {
        Optional<IdempotencyRecord> existingRecordOpt = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if (existingRecordOpt.isPresent()) {
            IdempotencyRecord existing = existingRecordOpt.get();
            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
                if (!existing.getRequestHash().equals(requestHash)) {
                    throw new RuntimeException("Idempotency key reused with a different request payload.");
                }
                return Optional.of(productMapper.toDTO(existing.getProduct()));
            }
            if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
                throw new RuntimeException("Request with this key is currently in progress. Please retry later.");
            }
        }
        return Optional.empty();
    }

    private void createProductOutboxEvent(Product product) {
        try {
            ProductCreatedEvent eventPayload = new ProductCreatedEvent(
                    product.getProductId(),
                    product.getSellerId(),
                    product.getTitle(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getType(),
                    product.getCategory().getCategoryId()
            );
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Product");
            outboxEvent.setAggregateId(product.getProductId().toString());
            outboxEvent.setEventType("ProductCreated");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for product creation", e);
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
        updateProductOutboxEvent(savedProduct);

        return productMapper.toDTO(savedProduct);
    }

    private void updateProductOutboxEvent(Product product) {
        try {
            ProductUpdatedEvent eventPayload = new ProductUpdatedEvent(
                    product.getProductId(),
                    product.getSellerId(),
                    product.getTitle(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getType(),
                    product.getCategory().getCategoryId()
            );
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Product");
            outboxEvent.setAggregateId(product.getProductId().toString());
            outboxEvent.setEventType("ProductUpdated");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for product update", e);
        }
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
        deleteProductOutboxEvent(productToDelete);
    }

    private void deleteProductOutboxEvent(Product product) {
        try {
            ProductDeletedEvent eventPayload = new ProductDeletedEvent(product.getProductId());
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Product");
            outboxEvent.setAggregateId(product.getProductId().toString());
            outboxEvent.setEventType("ProductDeleted");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for product deletion", e);
        }
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