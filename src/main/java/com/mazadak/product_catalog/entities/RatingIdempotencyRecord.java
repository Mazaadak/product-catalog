package com.mazadak.product_catalog.entities;

import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rating_idempotency_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RatingIdempotencyRecord extends BaseEntity {

    @Id
    private String idempotencyKey;

    @Column(nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdempotencyStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private ProductRating rating;
}