package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
