package com.example.Ordering.order_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponseDTO {
    private Long id;
    private String token_number;
    private String qr_data;
    private Long Total;
    private int number_of_times_user_tried_payment;
    private LocalDateTime created_at;
    private Order_Status orderStatus;
}
