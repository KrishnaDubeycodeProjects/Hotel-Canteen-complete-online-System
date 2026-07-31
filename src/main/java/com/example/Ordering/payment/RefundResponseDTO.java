package com.example.Ordering.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDTO {
    private Long id;
    private Long amount;
    private String reason;
    private Refund_status refundStatus;
    private LocalDateTime requested_at;
    private LocalDateTime processed_at;
}
