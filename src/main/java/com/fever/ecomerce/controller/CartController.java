package com.fever.ecomerce.controller;

import com.fever.ecomerce.dto.CartDto;
import com.fever.ecomerce.model.Cart;
import com.fever.ecomerce.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Cart> createCart() {
        return new ResponseEntity<>(cartService.createCart(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable String id) {
        return ResponseEntity.ok(cartService.getCart(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> addProducts(@PathVariable String id, @RequestBody CartDto request) {
        return ResponseEntity.ok(cartService.addProducts(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
