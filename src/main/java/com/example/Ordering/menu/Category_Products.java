package com.example.Ordering.menu;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Category_Products {
    @Id
    private Long id;
    private String product_name;
}
