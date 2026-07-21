package com.example.Ordering.order_details;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Order_track {
    @Id
    private Long id;
    private Order_Status orderStatus;
    private LocalDateTime last_update_time;

}
