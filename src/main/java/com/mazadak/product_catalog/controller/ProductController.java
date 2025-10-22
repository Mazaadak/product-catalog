package com.mazadak.product_catalog.controller;

import com.mazadak.product_catalog.dto.request.*;
import com.mazadak.product_catalog.dto.response.*;
import com.mazadak.product_catalog.service.ProductService;
import com.mazadak.product_catalog.util.IdempotencyUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping
    Page<ProductResponseDTO> getProductsByCriteria(
            @ModelAttribute ProductFilterDTO filter,
            Pageable pageable
    ) {
        return productService.getProductsByCriteria(filter, pageable);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-User-Id") UUID currentUserId,
            @RequestBody CreateProductRequestDTO createRequest) {

        String requestHash = IdempotencyUtil.calculateHash(createRequest);
        ProductResponseDTO createdProduct = productService.createProduct(idempotencyKey, requestHash, createRequest, currentUserId);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequestDTO updateRequest,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        ProductResponseDTO updatedProduct = productService.updateProduct(productId, updateRequest, currentUserId);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        productService.deleteProduct(productId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByIds(@RequestBody List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productService.getProductsByIds(productIds));
    }

}