package com.mazadak.product_catalog.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutboxEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;
    private boolean processed = false;
}