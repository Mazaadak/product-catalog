package com.mazadak.product_catalog.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_auctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAuction extends BaseEntity {

    @Id
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    private Long auctionId;

    private boolean isActive = false;
}