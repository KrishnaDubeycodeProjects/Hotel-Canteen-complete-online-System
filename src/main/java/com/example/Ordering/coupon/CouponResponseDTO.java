package com.example.Ordering.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private Long coupon_id;
    private String coupon_code;
    private Discount_Type discount_type;
    private long discount_value;
    private Long minimum_order;
    private LocalDateTime startAt;
    private LocalDateTime expiry;
    private boolean is_active;
    private Long total_discount_budget;
    private Long number_of_token;
}
