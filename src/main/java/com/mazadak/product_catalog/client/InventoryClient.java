package com.mazadak.product_catalog.client;

import com.mazadak.product_catalog.dto.client.AddInventoryRequest;
import com.mazadak.product_catalog.dto.client.InventoryDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("inventory-service")
public interface InventoryClient {
    @GetMapping("/inventories/exists/{productId}")
    ResponseEntity<Boolean> existsByProductId(@PathVariable UUID productId);

    @PostMapping("/inventories")
    ResponseEntity<InventoryDTO> addInventory(
            @RequestHeader("Idempotency-Key") @NotNull UUID idempotencyKey,
            @Valid @RequestBody AddInventoryRequest request);

    @DeleteMapping("/inventories/{productId}")
    ResponseEntity<Void> deleteInventory(@PathVariable @NotNull UUID productId);
}
