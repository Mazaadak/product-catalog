package com.mazadak.product_catalog.event.consumer;

import com.mazadak.product_catalog.dto.event.AuctionDeletedEvent;
import com.mazadak.product_catalog.dto.event.AuctionEndedEvent;
import com.mazadak.product_catalog.dto.event.AuctionStartedEvent;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.service.ProductAuctionService;
import com.mazadak.product_catalog.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class AuctionEventConsumer {
    @Bean
    public Consumer<AuctionDeletedEvent> auctionDeleted(ProductService productService, ProductAuctionService productAuctionService) {
        return auctionDeletedEvent -> {
            log.info("Consuming AuctionDeletedEvent {}", auctionDeletedEvent);

            productService.setProductType(auctionDeletedEvent.productId(), ProductType.NONE);
            log.info("Set productType to NONE for product {}", auctionDeletedEvent.productId());

            productAuctionService.markAuctionAsInactive(auctionDeletedEvent.productId(), auctionDeletedEvent.auctionId());
            log.info("Marked auction {} as inactive for product {}", auctionDeletedEvent.auctionId(), auctionDeletedEvent.productId());
        };
    }

    @Bean
    public Consumer<AuctionStartedEvent> auctionStarted(ProductAuctionService productAuctionService) {
        return auctionStartedEvent -> {
            log.info("Consuming AuctionStartedEvent {}", auctionStartedEvent);
            productAuctionService.markAuctionAsActive(auctionStartedEvent.productId(), auctionStartedEvent.auctionId());
            log.info("Marked auction {} active for product {}", auctionStartedEvent.auctionId(), auctionStartedEvent.productId());
        };
    }

    @Bean
    public Consumer<AuctionEndedEvent> auctionEnded(ProductAuctionService productAuctionService) {
        return auctionEndedEvent -> {
            log.info("Consuming AuctionEndedEvent {}", auctionEndedEvent);
            productAuctionService.markAuctionAsInactive(auctionEndedEvent.auction().productId(), auctionEndedEvent.auction().id());
            log.info("Marked auction {} inactive for product {}", auctionEndedEvent.auction().id(), auctionEndedEvent.auction().productId());
        };
    }
}
