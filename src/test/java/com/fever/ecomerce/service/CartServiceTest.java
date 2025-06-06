package com.fever.ecomerce.service;

import com.fever.ecomerce.dto.CartDto;
import com.fever.ecomerce.dto.ProductDto;
import com.fever.ecomerce.exception.CartNotFoundException;
import com.fever.ecomerce.model.Cart;
import com.fever.ecomerce.model.Product;
import com.fever.ecomerce.storage.LocalStorage;
import com.fever.ecomerce.utils.CartTimerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private LocalStorage localStorage;
    @Mock
    private CartTimerManager timerManager;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(localStorage, timerManager);
    }

    @Test
    void createCart_shouldSaveCartAndStartTimer() {
        Cart savedCart = new Cart();
        savedCart.setId("1");
        savedCart.setLastUpdated(LocalDateTime.now());

        when(localStorage.saveCart(any())).thenReturn(savedCart);

        Cart cart = cartService.createCart();

        assertNotNull(cart);
        assertEquals("1", cart.getId());
        verify(localStorage).saveCart(any());
        verify(timerManager).scheduleCartExpiration(eq("1"), any(Runnable.class));
    }

    @Test
    void getCart_whenCartExistsAndNotExpired_shouldReturnCart() {
        Cart cart = new Cart();
        cart.setId("1");
        cart.setLastUpdated(LocalDateTime.now());

        when(localStorage.findCartById("1")).thenReturn(cart);

        Cart found = cartService.getCart("1");

        assertNotNull(found);
        assertEquals("1", found.getId());
    }

    @Test
    void getCart_whenCartNotFound_shouldThrowException() {
        when(localStorage.findCartById("999")).thenReturn(null);

        assertThrows(CartNotFoundException.class, () -> cartService.getCart("999"));
    }


    @Test
    void addProducts_shouldAddAndSaveProducts() {
        Cart cart = new Cart();
        cart.setId("1");
        cart.setLastUpdated(LocalDateTime.now());

        when(localStorage.findCartById("1")).thenReturn(cart);
        when(localStorage.saveProduct(any())).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId("100");
            return p;
        });

        ProductDto dto = new ProductDto();
        dto.setAmount(5);
        dto.setDescription("Test product");

        CartDto cartDto = new CartDto();
        cartDto.setProducts(List.of(dto));

        Cart updatedCart = cartService.addProducts("1", cartDto);

        assertEquals(1, updatedCart.getProducts().size());
        assertEquals("Test product", updatedCart.getProducts().get(0).getDescription());
        assertEquals(5, updatedCart.getProducts().get(0).getAmount());
        verify(localStorage).saveProduct(any());
    }

    @Test
    void deleteCart_whenExists_shouldDelete() {
        when(localStorage.findCartById("1")).thenReturn(new Cart());

        cartService.deleteCart("1");

        verify(localStorage).deleteCartById("1");
    }

    @Test
    void deleteCart_whenNotFound_shouldThrowException() {
        when(localStorage.findCartById("999")).thenReturn(null);

        assertThrows(CartNotFoundException.class, () -> cartService.deleteCart("999"));
    }

    @Test
    void createCart_shouldStartTimer() {
        Cart savedCart = new Cart();
        savedCart.setId("1");
        savedCart.setLastUpdated(LocalDateTime.now());

        when(localStorage.saveCart(any())).thenReturn(savedCart);

        Cart cart = cartService.createCart();

        assertNotNull(cart);
        verify(localStorage).saveCart(any());
        verify(timerManager).scheduleCartExpiration(eq("1"), any(Runnable.class));
    }

    @Test
    void getCart_afterTimerExpiration_shouldDeleteCartAndThrowException() {
        Cart savedCart = new Cart();
        savedCart.setId("1");
        savedCart.setLastUpdated(LocalDateTime.now());

        when(localStorage.saveCart(any(Cart.class))).thenReturn(savedCart);

        doAnswer(invocation -> {
            Runnable onExpire = invocation.getArgument(1);
            onExpire.run();
            return null;
        }).when(timerManager).scheduleCartExpiration(eq("1"), any(Runnable.class));

        cartService.createCart();

        assertThrows(CartNotFoundException.class, () -> cartService.getCart("1"));
    }

}
