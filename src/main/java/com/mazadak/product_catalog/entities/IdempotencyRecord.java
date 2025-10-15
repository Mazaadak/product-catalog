package com.mazadak.product_catalog.entities;

import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "idempotency_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyRecord extends BaseEntity {

    @Id
    private String idempotencyKey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;


    @Column(name = "request_hash", nullable = false)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IdempotencyStatus status;

}