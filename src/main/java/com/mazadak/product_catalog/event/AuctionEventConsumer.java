package com.mazadak.product_catalog.event;

import com.mazadak.product_catalog.dto.event.AuctionEndedEvent;
import com.mazadak.product_catalog.dto.event.AuctionStartedEvent;
import com.mazadak.product_catalog.service.ProductAuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventConsumer {

    private final ProductAuctionService productAuctionService;

    @KafkaListener(topics = "auction.started", groupId = "product-catalog-group")
    public void handleAuctionStarted(AuctionStartedEvent event) {
        productAuctionService.markAuctionAsActive(event.getProductId(), event.getAuctionId());
    }

    @KafkaListener(topics = "auction.ended", groupId = "product-catalog-group")
    public void handleAuctionEnded(AuctionEndedEvent event) {
        productAuctionService.markAuctionAsInactive(event.getProductId());
    }
}
