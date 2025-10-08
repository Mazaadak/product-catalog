package com.mazadak.product_catalog.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_id", columnList = "productId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Lob
    @Column(nullable = false)
    private String imageUri;

    private Boolean isPrimary = false;

    private Integer position = 0;
}
