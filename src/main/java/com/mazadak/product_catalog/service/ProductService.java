package com.mazadak.product_catalog.service;

import com.mazadak.product_catalog.dto.ProductDTO;
import com.mazadak.product_catalog.entities.Product;
import com.mazadak.product_catalog.entities.ProductStatus;
import com.mazadak.product_catalog.mapper.ProductMapper;
import com.mazadak.product_catalog.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

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

    public ProductDTO createProduct(Product product) {
        return productMapper.ToDTO(productRepository.save(product));
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
        productRepository.findById(productId).ifPresent(product -> {
            product.setStatus(ProductStatus.DELETED);
            productRepository.save(product);
        });
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
