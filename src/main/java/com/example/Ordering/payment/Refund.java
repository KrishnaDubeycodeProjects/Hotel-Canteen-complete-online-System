package com.example.Ordering.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Refund {
    @Id
    private Long id;
    private Long amount;
    private String reason;
    private Refund_status refundStatus;
    private LocalDateTime requested_at;
    private LocalDateTime processed_at;
}
