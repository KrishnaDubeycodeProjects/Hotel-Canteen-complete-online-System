package com.example.Ordering.order_details;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


@Entity
public class Orders {
    @Id
    private Long id;

    private String token_number;

    private String qr_data;
       private Long Total;
    private int number_of_times_user_tried_payment;

    private LocalDateTime created_at;

}
