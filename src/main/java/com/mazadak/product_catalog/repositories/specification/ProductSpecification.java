package com.mazadak.product_catalog.repositories.specification;

import com.mazadak.product_catalog.dto.request.ProductFilterDTO;
import com.mazadak.product_catalog.dto.response.CategoryDTO;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.ProductRating;
import com.mazadak.product_catalog.entities.enums.ProductStatus;
import com.mazadak.product_catalog.entities.enums.ProductType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {
    private static Specification<Product> hasId(UUID id) {
        return (root, query, builder) -> {
            if (id == null) return null;
            return builder.equal(root.get("productId"), id);
        };
    }

    public static Specification<Product> hasSellerId(UUID sellerId) {
        return (root, query, builder) -> {
            if (sellerId == null) return null;
            return builder.equal(root.get("sellerId"), sellerId);
        };
    }

    public static Specification<Product> containsTitle(String title) {
        return (root, query, builder) -> {
            if (title == null) return null;
            return builder.like(builder.lower(root.get("title")), "%" + title + "%");
        };
    }

    public static Specification<Product> containsDescription(String description) {
        return (root, query, builder) -> {
            if (description == null) return null;
            return builder.like(builder.lower(root.get("description")), "%" + description + "%");
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min == null && max == null) return null;
            if (min == null) return builder.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return builder.greaterThanOrEqualTo(root.get("price"), min);
            return builder.between(root.get("price"), min, max);
        };
    }

    public static Specification<Product> hasType(ProductType type) {
        return (root, query, builder) -> {
            if (type == null) return null;
            return builder.equal(root.get("type"), type);
        };
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, builder) -> {
            if (status == null) return null;
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Product> isInAnyCategory(List<Long> categories) {
        return (root, query, builder) -> {
            if (categories == null || categories.isEmpty()) return null;
            return root.get("category").get("categoryId").in(categories);
        };
    }

    public static Specification<Product> hasRatingLowerThan(Integer rating) {
        return (root, query, builder) -> {
            if (rating == null || rating > 5 || rating < 1) return null;

            Subquery<Double> subquery = query.subquery(Double.class);
            Root<ProductRating> ratingRoot = subquery.from(ProductRating.class);
            subquery.select(builder.avg(ratingRoot.get("rating")));
            subquery.where(builder.equal(ratingRoot.get("product"), root));

            return builder.lessThanOrEqualTo(subquery, rating.doubleValue());
        };
    }

    public static Specification<Product> fromFilter(ProductFilterDTO filter) {
        return Specification.allOf(
                hasId(filter.id()),
                hasSellerId(filter.sellerId()),
                containsTitle(filter.title()),
                containsDescription(filter.description()),
                hasPriceBetween(filter.minPrice(), filter.maxPrice()),
                hasType(filter.type()),
                hasStatus(filter.status()),
                isInAnyCategory(filter.categories()),
                hasRatingLowerThan(filter.maxRating())
        );
    }
}
