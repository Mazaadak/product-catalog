package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.entities.OutboxEvent;
import com.mazadak.product_catalog.repositories.OutboxEventRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayService {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Map<String, String> topicMappings = new HashMap<>();

    @PostConstruct
    public void initializeTopicMappings() {
        topicMappings.put("PRODUCT", "product-events");
        topicMappings.put("RATING", "rating-events");
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processAndRelayOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findTop1000ByProcessedFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }
        log.info("Found {} events to relay from the outbox.", events.size());

        for (OutboxEvent event : events) {
            try {
                String topic = getTopicForEvent(event);

                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());
                event.setProcessed(true);

            } catch (Exception e) {
                log.error("Error relaying outbox event with ID: {}. Rolling back the batch.", event.getId(), e);
                throw new RuntimeException("Failed to send event to Kafka, rolling back batch.", e);
            }
        }
        outboxRepository.saveAll(events);
    }

    private String getTopicForEvent(OutboxEvent event) {
        return topicMappings.getOrDefault(event.getAggregateType().toUpperCase(), "default-topic");
    }
}