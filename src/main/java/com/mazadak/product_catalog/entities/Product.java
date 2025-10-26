package com.mazadak.product_catalog.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mazadak.product_catalog.entities.enums.ListingStatus;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.entities.enums.ProductType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    private UUID sellerId;

    @Column(length = 100, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProductType type = ProductType.NONE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProductStatus status = ProductStatus.INACTIVE; // create drafts by default

    @Enumerated(EnumType.STRING)
    private ListingStatus listingStatus = ListingStatus.NONE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductRating> ratings = new ArrayList<>();

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private IdempotencyRecord idempotencyRecord;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductAuction productAuction;
}