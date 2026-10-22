package com.example.Ordering.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCode(String couponCode);

    List<Coupon> findByActiveTrue();

    List<Coupon> findByExpiryBefore(LocalDateTime now);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(c.couponId), 0) FROM Coupon c")
    Long findMaxId();
}
