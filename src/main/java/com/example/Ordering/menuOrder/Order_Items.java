package com.example.Ordering.menuOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NonNull;

@Entity
public class Order_Items {
    @Id
    private Long id;
    @Column(nullable = false)
    private Integer dish_quantity;
    @Column(nullable = false)
    private Integer price;
    private Long subTotal;

}
