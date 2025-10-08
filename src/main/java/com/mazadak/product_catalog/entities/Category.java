package com.mazadak.product_catalog.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    // FIELD TO ADD: References the ID of the parent category.
    // If null, this is a top-level (root) category.
    @Column(name = "parent_id")
    private Long parentId;
}
