package com.example.Ordering.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long total_amount;
    private String transaction_id;
    private LocalDateTime paid_at;
    private Payment_method paymentMethod;
}
