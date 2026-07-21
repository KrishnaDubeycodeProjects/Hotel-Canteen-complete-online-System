package com.example.Ordering.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    private Long id;
    private Long total_amount;
    private String transaction_id;
    private  LocalDateTime paid_at;
    private Payment_method paymentMethod;
}
