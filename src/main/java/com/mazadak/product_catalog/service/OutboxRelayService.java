package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.entities.OutboxEvent;
import com.mazadak.product_catalog.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayService {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processAndRelayOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} events to relay from the outbox.", events.size());

        for (OutboxEvent event : events) {
            try {
                String topic = event.getAggregateType().equalsIgnoreCase("Product") ? "product-events" : "default-topic";
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());
                event.setProcessed(true);

            } catch (Exception e) {
                log.error("Error relaying outbox event with ID: {}", event.getId(), e);
            }
        }

        outboxRepository.saveAll(events);
    }
}