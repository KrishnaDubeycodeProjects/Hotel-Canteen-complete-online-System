package com.example.Ordering.menu;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Menu_Category {
    @Id
    private Long id;
    private String cateogry_name;
}
