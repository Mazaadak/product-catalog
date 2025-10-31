package com.mazadak.product_catalog.event.consumer;

import com.mazadak.product_catalog.dto.event.AuctionDeletedEvent;
import com.mazadak.product_catalog.dto.event.InventoryDeletedEvent;
import com.mazadak.product_catalog.entities.enums.ProductType;
import com.mazadak.product_catalog.service.ProductAuctionService;
import com.mazadak.product_catalog.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class InventoryEventConsumer {
    @Bean
    public Consumer<InventoryDeletedEvent> inventoryDeleted(ProductService productService) {
        return inventoryDeletedEvent -> {
            log.info("Consuming InventoryDeletedEvent {}", inventoryDeletedEvent);

            productService.setProductType(inventoryDeletedEvent.productId(), ProductType.NONE);
            log.info("Set productType to NONE for product {}", inventoryDeletedEvent.productId());
        };
    }
}
