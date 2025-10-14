package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.entity.ProductDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.IdempotencyRecord;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import com.mazadak.product_catalog.repositories.IdempotencyRecordRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public List<ProductDTO> getAllProducts() {
        return productMapper.ToDTO(productRepository.findAll());
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productMapper.ToDTO(productRepository.findByCategory_CategoryId(categoryId));
    }

    public List<ProductDTO> getProductsByStatus(String status) {
        return productMapper.ToDTO(productRepository.findByStatus(status));
    }

    public ProductDTO getProductById(Long productId) {
        return productMapper.ToDTO(productRepository.findById(productId).orElse(null));
    }

    @Transactional
    public ProductDTO createProduct(String idempotencyKey, String requestHash, ProductDTO productDTO) {

        Optional<IdempotencyRecord> existingRecordOpt = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRecordOpt.isPresent()) {
            IdempotencyRecord existing = existingRecordOpt.get();

            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
                if (!existing.getRequestHash().equals(requestHash)) {
                    throw new RuntimeException("Idempotency key reused with a different request payload.");
                }

                Product existingProduct = existing.getProduct();
                if (existingProduct == null) {
                    throw new ResourceNotFoundException("Completed idempotent product not found.");
                }
                return productMapper.ToDTO(existingProduct);
            }

            if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
                throw new RuntimeException("Request with this key is currently in progress. Please retry later.");
            }
        }

        IdempotencyRecord newRecord = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .status(IdempotencyStatus.IN_PROGRESS)
                .build();
        idempotencyRecordRepository.save(newRecord);

        try {
            Product productEntity = productMapper.ToEntity(productDTO);

            Category category = categoryRepository.findById(productDTO.getCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + productDTO.getCategory().getCategoryId()));
            productEntity.setCategory(category);

            Product savedProduct = productRepository.save(productEntity);
            newRecord.setProduct(savedProduct);
            newRecord.setStatus(IdempotencyStatus.COMPLETED);
            idempotencyRecordRepository.save(newRecord);

            return productMapper.ToDTO(savedProduct);

        } catch (Exception e) {
            throw e;
        }
    }



    public ProductDTO updateProduct(Long productId, Product product) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct != null) {
            existingProduct.setTitle(product.getTitle());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setType(product.getType());
            existingProduct.setStatus(product.getStatus());
            return productMapper.ToDTO(productRepository.save(existingProduct));
        }
        return null;
    }

    public void deleteProduct(Long productId) {

    }


    public ProductDTO getProductBySellerIdAndProductId(Long sellerId, Long productId) {
        return productMapper.ToDTO(productRepository.findProductBySellerIdAndProductId(sellerId, productId));
    }


    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::ToDTO);
    }

    public Page<ProductDTO> getProductsBySellerId(Long sellerId, Pageable pageable) {
        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);
        return products.map(productMapper::ToDTO);
    }
}
