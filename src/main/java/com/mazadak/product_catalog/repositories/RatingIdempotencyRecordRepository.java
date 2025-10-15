package com.mazadak.product_catalog.repositories;

import com.mazadak.product_catalog.entities.RatingIdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingIdempotencyRecordRepository extends JpaRepository<RatingIdempotencyRecord, Long> {
    Optional<RatingIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
