package com.mazadak.product_catalog.client;

import com.mazadak.product_catalog.dto.client.AuctionResponse;
import com.mazadak.product_catalog.dto.client.CreateAuctionRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("auctions")
public interface AuctionClient {
    @GetMapping("/auctions/exists/{productId}")
    ResponseEntity<Boolean> existsByProductId(@PathVariable UUID productId);

    @PostMapping("/auctions")
    ResponseEntity<AuctionResponse> createAuction(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @Valid @RequestBody CreateAuctionRequest dto);

    @DeleteMapping("/auctions/{id}")
    ResponseEntity<Void> deleteAuction(@PathVariable UUID id);
}
