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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortField,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortField));
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsBySellerId(@PathVariable Long sellerId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsBySellerId(sellerId, pageable));
    }

    @GetMapping("/seller/{sellerId}/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductBySellerIdAndProductId(
            @PathVariable Long sellerId, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductBySellerIdAndProductId(sellerId, productId));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody CreateProductRequestDTO createRequest) {

        String requestHash = IdempotencyUtil.calculateHash(createRequest);
        ProductResponseDTO createdProduct = productService.createProduct(idempotencyKey, requestHash, createRequest, currentUserId);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequestDTO updateRequest,
            @RequestHeader("X-User-Id") Long currentUserId) {

        ProductResponseDTO updatedProduct = productService.updateProduct(productId, updateRequest, currentUserId);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long currentUserId) {

        productService.deleteProduct(productId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByIds(@RequestBody List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productService.getProductsByIds(productIds));
    }

}