package com.fever.ecomerce.service;

import com.fever.ecomerce.dto.CartDto;
import com.fever.ecomerce.dto.ProductDto;
import com.fever.ecomerce.exception.CartNotFoundException;
import com.fever.ecomerce.model.Cart;
import com.fever.ecomerce.model.Product;
import com.fever.ecomerce.storage.LocalStorage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CartService {
    
    private final LocalStorage localStorage;

    public CartService(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    public Cart createCart() {
        Cart cart = new Cart();
        return localStorage.saveCart(cart);
    }

    public Cart getCart(String id) {
        Cart cart = localStorage.findCartById(id);
        if (cart == null) {
            throw new CartNotFoundException(id);
        }
        return cart;
    }

    public Cart addProducts(String id, CartDto request) {
        Cart cart = getCart(id);

        for (ProductDto dto : request.getProducts()) {
            Product product = new Product();
            product.setAmount(dto.getAmount());
            product.setDescription(dto.getDescription());
            product = localStorage.saveProduct(product);
            cart.getProducts().add(product);
        }
        cart.setLastUpdated(LocalDateTime.now());
        return cart;
    }

    public void deleteCart(String id) {
        if (localStorage.findCartById(id) == null) {
            throw new CartNotFoundException(id);
        }
        localStorage.deleteCartById(id);
    }
}
