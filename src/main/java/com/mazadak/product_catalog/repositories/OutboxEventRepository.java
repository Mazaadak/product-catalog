package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}
