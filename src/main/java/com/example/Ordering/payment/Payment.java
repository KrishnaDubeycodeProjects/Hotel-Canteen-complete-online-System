package com.example.Ordering.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private Long id;

    private Long total_amount;
    private String transaction_id;
    private LocalDateTime paid_at = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Payment_method paymentMethod = Payment_method.UPI;

    private String status = "SUCCESS";
}
