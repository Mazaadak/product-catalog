package com.mazadak.product_catalog.controller;

import com.mazadak.product_catalog.dto.entity.ProductRatingDTO;
import com.mazadak.product_catalog.dto.request.CreateRatingRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateRatingRequestDTO;
import com.mazadak.product_catalog.dto.response.RatingResponseDTO;
import com.mazadak.product_catalog.service.ProductRatingService;
import com.mazadak.product_catalog.util.IdempotencyUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductRatingController {

    private final ProductRatingService productRatingService;

    @PostMapping("/{productId}/ratings")
    public ResponseEntity<RatingResponseDTO> createProductRating(
            @PathVariable UUID productId,
            @RequestBody CreateRatingRequestDTO createRequest,
            @RequestHeader("X-User-Id") UUID currentUserId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        String requestHash = IdempotencyUtil.calculateHash(createRequest);
        RatingResponseDTO newRating = productRatingService.createProductRating(idempotencyKey, requestHash,
                productId, currentUserId, createRequest
        );
        return ResponseEntity.ok(newRating);
    }

    @PutMapping("/ratings/{ratingId}")
    public ResponseEntity<RatingResponseDTO> updateProductRating(
            @PathVariable Long ratingId,
            @RequestBody UpdateRatingRequestDTO updateRequest,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        RatingResponseDTO updatedRating = productRatingService.updateProductRating(
                ratingId, currentUserId, updateRequest
        );
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> deleteProductRating(
            @PathVariable Long ratingId,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        productRatingService.deleteProductRating(ratingId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/ratings")
    public ResponseEntity<Page<RatingResponseDTO>> getRatingsByProductId(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ratingId") String sortField,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<RatingResponseDTO> ratings = productRatingService.getRatingsByProductId(productId, pageable);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/ratings/{ratingId}")
    public ResponseEntity<RatingResponseDTO> getRatingById(@PathVariable Long ratingId) {
        RatingResponseDTO rating = productRatingService.getProductRatingById(ratingId);
        if (rating == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/ratings/user/{userId}")
    public ResponseEntity<Page<RatingResponseDTO>> getRatingsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ratingId") String sortField,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<RatingResponseDTO> ratings = productRatingService.getRatingsByUserId(userId, pageable);
        return ResponseEntity.ok(ratings);
    }
}
