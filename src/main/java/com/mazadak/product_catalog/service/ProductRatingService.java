package com.mazadak.product_catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.product_catalog.dto.entity.ProductRatingDTO;
import com.mazadak.product_catalog.dto.event.RatingCreatedEvent;
import com.mazadak.product_catalog.dto.event.RatingDeletedEvent;
import com.mazadak.product_catalog.dto.event.RatingUpdatedEvent;
import com.mazadak.product_catalog.dto.request.CreateRatingRequestDTO;
import com.mazadak.product_catalog.dto.request.UpdateRatingRequestDTO;
import com.mazadak.product_catalog.dto.response.ProductResponseDTO;
import com.mazadak.product_catalog.dto.response.RatingResponseDTO;
import com.mazadak.product_catalog.entities.*;
import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import com.mazadak.product_catalog.mapper.ProductRatingMapper;
import com.mazadak.product_catalog.repositories.OutboxEventRepository;
import com.mazadak.product_catalog.repositories.ProductRatingRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import com.mazadak.product_catalog.repositories.RatingIdempotencyRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductRatingService {
    private final ProductRatingRepository productRatingRepository;
    private final ProductRatingMapper productRatingMapper;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final RatingIdempotencyRecordRepository ratingIdempotencyRecordRepository;
    private final OutboxEventRepository outboxRepository;

    public List<RatingResponseDTO> getAllProductRatings() {
        return productRatingMapper.toDTOList(productRatingRepository.findAll());
    }

    public RatingResponseDTO getProductRatingById(Long ratingId) {
        return productRatingMapper.toDTO(productRatingRepository.findById(ratingId).orElse(null));
    }

    @Transactional
    public RatingResponseDTO createProductRating(
            String idempotencyKey,
            String requestHash,
            UUID productId,
            UUID currentUserId,
            CreateRatingRequestDTO createRequest) {

        Optional<RatingResponseDTO> existingRecordOpt = handleIdempotencyCheck(idempotencyKey, requestHash);
        if (existingRecordOpt.isPresent()) {
            return existingRecordOpt.get();
        }

        RatingIdempotencyRecord newRecord = RatingIdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .status(IdempotencyStatus.IN_PROGRESS)
                .build();
        ratingIdempotencyRecordRepository.save(newRecord);

        ProductRating savedRating = null;

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            if (product.getSellerId().equals(currentUserId)) {
                throw new IllegalStateException("You cannot rate your own product.");
            }

            if (productRatingRepository.existsByProduct_ProductIdAndUserId(productId, currentUserId)) {
                throw new IllegalStateException("You have already submitted a rating for this product.");
            }

            ProductRating newRating = productRatingMapper.toEntity(createRequest);
            newRating.setProduct(product);
            newRating.setUserId(currentUserId);
            savedRating = productRatingRepository.save(newRating);

            createRatingOutboxEvent(savedRating);

            newRecord.setRating(savedRating);
            newRecord.setStatus(IdempotencyStatus.COMPLETED);
            ratingIdempotencyRecordRepository.save(newRecord);

            return productRatingMapper.toDTO(savedRating);

        } catch (Exception e) {
            if (savedRating == null) {
                newRecord.setStatus(IdempotencyStatus.FAILED);
                ratingIdempotencyRecordRepository.save(newRecord);
            }
            throw new RuntimeException(e);
        }
    }


    private Optional<RatingResponseDTO> handleIdempotencyCheck(String idempotencyKey, String requestHash) {
        Optional<RatingIdempotencyRecord> existingRecordOpt = ratingIdempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if (existingRecordOpt.isPresent()) {
            RatingIdempotencyRecord existing = existingRecordOpt.get();
            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
                if (!existing.getRequestHash().equals(requestHash)) {
                    throw new RuntimeException("Idempotency key reused with a different request payload.");
                }
                return Optional.of(productRatingMapper.toDTO(existing.getRating()));
            }
            if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
                throw new RuntimeException("Request with this key is currently in progress. Please retry later.");
            }
        }
        return Optional.empty();
    }

    private void createRatingOutboxEvent(ProductRating savedRating) {
        try {
            RatingCreatedEvent eventPayload = new RatingCreatedEvent(
                    savedRating.getRatingId(),
                    savedRating.getProduct().getProductId(),
                    savedRating.getProduct().getSellerId(),
                    savedRating.getUserId(),
                    savedRating.getRating()
            );
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Rating");
            outboxEvent.setAggregateId(savedRating.getRatingId().toString());
            outboxEvent.setEventType("RatingCreated");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for rating creation", e);
        }
    }


    @Transactional
    public RatingResponseDTO updateProductRating(Long ratingId, UUID currentUserId, UpdateRatingRequestDTO updateRequest) {
        ProductRating existingRating = productRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + ratingId));

        if (!existingRating.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to update this rating.");
        }

        productRatingMapper.updateEntityFromDto(updateRequest, existingRating);

        ProductRating savedRating = productRatingRepository.save(existingRating);

        updateRatingOutboxEvent(savedRating);

        return productRatingMapper.toDTO(savedRating);
    }

    private void updateRatingOutboxEvent(ProductRating productRating) {
        try {
            RatingUpdatedEvent eventPayload = new RatingUpdatedEvent(
                    productRating.getRatingId(),
                    productRating.getProduct().getProductId(),
                    productRating.getUserId(),
                    productRating.getRating(),
                    productRating.getReviewText()
            );
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Rating");
            outboxEvent.setAggregateId(productRating.getRatingId().toString());
            outboxEvent.setEventType("RatingUpdated");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for rating update", e);
        }
    }
    @Transactional
    public void deleteProductRating(Long ratingId, UUID currentUserId) {
        ProductRating rating = productRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + ratingId));
        if (!rating.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to delete this rating.");
        }
        deleteRatingOutboxEvent(rating);
        productRatingRepository.deleteById(ratingId);
    }

    private void deleteRatingOutboxEvent(ProductRating ratingToDelete) {
        try {
            RatingDeletedEvent eventPayload = new RatingDeletedEvent(
                    ratingToDelete.getRatingId(),
                    ratingToDelete.getProduct().getProductId(),
                    ratingToDelete.getUserId()
            );
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Rating");
            outboxEvent.setAggregateId(ratingToDelete.getRatingId().toString());
            outboxEvent.setEventType("RatingDeleted");
            outboxEvent.setPayload(payload);

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating outbox event for rating deletion", e);
        }
    }
    public Page<RatingResponseDTO> getRatingsByProductId(UUID productId, Pageable pageable) {
        return productRatingRepository.findByProduct_ProductId(productId, pageable)
                .map(productRatingMapper::toDTO);
    }

    public Page<RatingResponseDTO> getRatingsByUserId(UUID userId, Pageable pageable) {
        return productRatingRepository.findByUserId(userId, pageable)
                .map(productRatingMapper::toDTO);
    }
}
