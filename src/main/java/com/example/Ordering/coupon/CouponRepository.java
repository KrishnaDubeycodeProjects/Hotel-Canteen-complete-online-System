package com.example.Ordering.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCoupon_code(String couponCode);

    List<Coupon> findByIs_activeTrue();

    List<Coupon> findByExpiryBefore(LocalDateTime now);
}
