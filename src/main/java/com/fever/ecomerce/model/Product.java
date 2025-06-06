package com.fever.ecomerce.model;

import lombok.Data;

@Data
public class Product {
    private String id;
    private String description;
    private Double amount;
}