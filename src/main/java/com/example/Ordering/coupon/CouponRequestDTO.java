package com.example.Ordering.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** What an admin submits to create/update a coupon. is_active is server-controlled, so it's omitted. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDTO {
    private String coupon_code;
    private Discount_Type discount_type;
    private long discount_value;
    private Long minimum_order;
    private LocalDateTime startAt;
    private LocalDateTime expiry;
    private Long total_discount_budget;
    private Long number_of_token;
}
