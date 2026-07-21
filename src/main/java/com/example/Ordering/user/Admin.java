package com.example.Ordering.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {
    @Id
    private Integer id;
    private Long phone_no;
}
