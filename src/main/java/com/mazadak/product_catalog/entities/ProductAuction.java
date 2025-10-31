package com.mazadak.product_catalog.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_auctions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAuction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID productId;

    private UUID auctionId;

    private boolean isActive = false;
}