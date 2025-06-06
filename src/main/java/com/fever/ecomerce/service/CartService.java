package com.fever.ecomerce.service;

import com.fever.ecomerce.dto.CartDto;
import com.fever.ecomerce.dto.ProductDto;
import com.fever.ecomerce.exception.CartNotFoundException;
import com.fever.ecomerce.model.Cart;
import com.fever.ecomerce.model.Product;
import com.fever.ecomerce.storage.LocalStorage;
import com.fever.ecomerce.utils.CartTimerManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CartService {
    
    private final LocalStorage localStorage;
    private final CartTimerManager cartTimerManager;

    public CartService(LocalStorage localStorage, CartTimerManager cartTimerManager) {
        this.localStorage = localStorage;
        this.cartTimerManager = cartTimerManager;
    }

    public Cart createCart() {
        Cart cart = new Cart();
        cart = localStorage.saveCart(cart);
        final String cartId = cart.getId();
        cartTimerManager.scheduleCartExpiration(cartId, () -> {});
        return cart;
    }

    public Cart getCart(String id) {
        Cart cart = localStorage.findCartById(id);
        if (cart == null) {
            throw new CartNotFoundException(id);
        }
        final String cartId = cart.getId();
        cartTimerManager.scheduleCartExpiration(cartId, () -> {});
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
        final String cartId = cart.getId();
        cartTimerManager.scheduleCartExpiration(cartId, () -> {});
        return cart;
    }

    public void deleteCart(String id) {
        if (localStorage.findCartById(id) == null) {
            throw new CartNotFoundException(id);
        }
        localStorage.deleteCartById(id);
        cartTimerManager.cancelTimer(id);
    }
}
