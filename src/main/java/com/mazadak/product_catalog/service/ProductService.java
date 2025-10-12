package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.ProductDTO;
import com.mazadak.product_catalog.entities.Category;
import com.mazadak.product_catalog.entities.IdempotencyRecord;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.enums.IdempotencyStatus;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.CategoryRepository;
import com.mazadak.product_catalog.repositories.IdempotencyRecordRepository;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public ProductDTO createProduct(String idempotencyKey, String requestHash, Product product) {
        Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {
            IdempotencyRecord existing = existingRecord.get();
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new RuntimeException("Idempotency key reused with different request body");
            }

            Product existingProduct = existing.getProduct();
            if (existingProduct == null) throw new RuntimeException("Product not found for existing idempotency key");
            return productMapper.ToDTO(existingProduct);
        }

        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);

        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setRequestHash(requestHash);
        record.setProduct(saved);
        record.setStatus(IdempotencyStatus.COMPLETED);

        idempotencyRecordRepository.save(record);

        return productMapper.ToDTO(saved);
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
