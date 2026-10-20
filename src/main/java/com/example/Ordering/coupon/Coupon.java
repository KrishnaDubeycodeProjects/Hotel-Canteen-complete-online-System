package com.example.Ordering.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Coupon {
    @Id
    private Long couponId;
    private String couponCode;
    private Discount_Type discountType;
    private long discountValue;
    private Long minimumOrder;

    private LocalDateTime startAt;
    private LocalDateTime expiry;
    private boolean active;

    private Long totalDiscountBudget;
    private Long numberOfToken;
    // maximum should be total / number and  if percentage is there
    // then like 50% upto 80rs like that coupon becomes



}
