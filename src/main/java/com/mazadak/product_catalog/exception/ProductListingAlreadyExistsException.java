package com.mazadak.product_catalog.exception;

public class ProductListingAlreadyExistsException extends RuntimeException {
    public ProductListingAlreadyExistsException(String message) {
        super(message);
    }
}
