package com.mazadak.product_catalog.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @ManyToOne(fetch = FetchType.EAGER)  // ADD THIS
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProductType type = ProductType.NONE;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)  // TODO: CHANGE TO LAZE
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)  // TODO: CHANGE TO LAZE
    private List<ProductRating> ratings = new ArrayList<>();

    @OneToOne(mappedBy = "product", fetch = FetchType.EAGER)  // TODO: CHANGE TO LAZE
    private IdempotencyRecord idempotencyRecord;
}