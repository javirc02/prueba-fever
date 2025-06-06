package com.fever.ecomerce.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private String id;
    private List<Product> products;
    private LocalDateTime lastUpdated;

    public Cart (){
        this.products = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
    }
}