package com.example.Ordering.order_details;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


@Entity
public class Orders {
    @Id
    private Long id;

    private String tokenNumber;

    private String qrData;
    private Long total;
    private int numberOfTimesUserTriedPayment;

    private LocalDateTime createdAt;

}
