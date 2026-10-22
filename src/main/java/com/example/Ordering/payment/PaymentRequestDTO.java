package com.example.Ordering.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** transaction_id/paid_at come from the payment gateway/server, not the client. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long orderId;
    private Long total_amount;
    private Payment_method paymentMethod = Payment_method.UPI;
}
