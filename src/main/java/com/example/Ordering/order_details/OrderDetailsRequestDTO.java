package com.example.Ordering.order_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** token_number/qr_data/created_at are generated server-side, so they're not requested from the client. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsRequestDTO {
    private Long Total;
}
