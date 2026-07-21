package com.example.Ordering.menu;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Menu_image {
    @Id
    private Long id;

    private Byte[] image;


}
