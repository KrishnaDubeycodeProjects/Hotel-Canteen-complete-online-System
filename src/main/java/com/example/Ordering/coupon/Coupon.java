package com.example.Ordering.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Coupon {
    @Id
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
    // maximum should be total / number and  if percentage is there
    // then like 50% upto 80rs like that coupon becomes



}
