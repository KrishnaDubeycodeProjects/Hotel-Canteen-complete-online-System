package com.example.Ordering.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    @Id
    private Long id;

    private Long orderId;
    private Long amount;
    private String reason;

    @Enumerated(EnumType.STRING)
    private Refund_status refundStatus = Refund_status.ACCEPTED;

    private String transactionId;
    private LocalDateTime requested_at = LocalDateTime.now();
    private LocalDateTime processed_at = LocalDateTime.now();
}
