package com.fever.ecomerce.storage;

import com.fever.ecomerce.model.Cart;
import com.fever.ecomerce.model.Product;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LocalStorage {

    private final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    private final AtomicInteger cartIdSequence = new AtomicInteger(1);
    private final AtomicInteger productIdSequence = new AtomicInteger(1);

    public Cart saveCart(Cart cart) {
        String id = String.valueOf(cartIdSequence.getAndIncrement());
        cart.setId(id);
        carts.put(id, cart);
        return cart;
    }

    public Cart findCartById(String id) {
        return carts.get(id);
    }

    public void deleteCartById(String id) {
        carts.remove(id);
    }

    public Product saveProduct(Product product) {
        String id = String.valueOf(productIdSequence.getAndIncrement());
        product.setId(id);
        products.put(id, product);
        return product;
    }
}
