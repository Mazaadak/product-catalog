package com.mazadak.product_catalog.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("payment")
public interface PaymentClient {
    @GetMapping("/api/onboarding/get-account/{sellerId}")
    ResponseEntity<String> getConnectedAccountId(@PathVariable UUID sellerId);
}
