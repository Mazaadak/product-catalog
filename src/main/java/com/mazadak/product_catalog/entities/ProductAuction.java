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
    private UUID productId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    private Long auctionId;

    private boolean isActive = false;
}