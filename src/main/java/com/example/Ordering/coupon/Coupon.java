package com.example.Ordering.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    private Long couponId;

    @Column(unique = true, nullable = false)
    private String couponCode;

    private Discount_Type discountType = Discount_Type.FLAT;

    private Long discountValue;
    private Long upto;
    private Long minimumOrder = 0L;

    private LocalDateTime startAt = LocalDateTime.now();
    private LocalDateTime expiry;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "is_active")
    private boolean isActive = true;

    private Long count = 0L;
    private Long numberOfToken = 1000L;
}
