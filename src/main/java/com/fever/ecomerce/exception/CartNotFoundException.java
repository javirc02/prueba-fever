package com.fever.ecomerce.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String id) {
        super("Cart not found: " + id);
    }
}