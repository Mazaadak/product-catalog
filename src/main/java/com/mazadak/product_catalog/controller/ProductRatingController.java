package com.mazadak.product_catalog.controller;

import com.mazadak.product_catalog.dto.ProductRatingDTO;
import com.mazadak.product_catalog.service.ProductRatingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductRatingController {

    private final ProductRatingService productRatingService;

    @PostMapping("/ratings")
    public ResponseEntity<ProductRatingDTO> createProductRating(@RequestBody ProductRatingDTO productRatingDTO) {
        return ResponseEntity.ok(productRatingService.createProductRating(productRatingDTO));
    }

    @PutMapping("/ratings")
    public ResponseEntity<ProductRatingDTO> updateProductRating(@RequestBody ProductRatingDTO productRatingDTO) {
        return ResponseEntity.ok(productRatingService.updateProductRating(productRatingDTO));
    }

    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> deleteProductRating(@PathVariable Long ratingId) {
        productRatingService.deleteProductRating(ratingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/ratings")
    public ResponseEntity<Page<ProductRatingDTO>> getRatingsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ratingId") String sortField,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<ProductRatingDTO> ratings = productRatingService.getRatingsByProductId(productId, pageable);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/ratings/{ratingId}")
    public ResponseEntity<ProductRatingDTO> getRatingById(@PathVariable Long ratingId) {
        ProductRatingDTO rating = productRatingService.getProductRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/ratings/user/{userId}")
    public ResponseEntity<Page<ProductRatingDTO>> getRatingsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ratingId") String sortField,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<ProductRatingDTO> ratings = productRatingService.getRatingsByUserId(userId, pageable);
        return ResponseEntity.ok(ratings);
    }
}
