package com.example.Ordering.coupon;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getActiveCoupons() {
        return couponRepository.findByActiveTrue();
    }

    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getCouponCode() == null || coupon.getCouponCode().trim().isEmpty()) {
            throw new BadRequestException("Coupon code is required");
        }
        if (coupon.getCouponId() == null) {
            coupon.setCouponId(couponRepository.findMaxId() + 1);
        }
        coupon.setCouponCode(coupon.getCouponCode().trim().toUpperCase());
        if (couponRepository.findByCouponCode(coupon.getCouponCode()).isPresent()) {
            throw new BadRequestException("Coupon code already exists");
        }
        if (coupon.getExpiry() == null) {
            coupon.setExpiry(LocalDateTime.now().plusMonths(6));
        }
        return couponRepository.save(coupon);
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    public Map<String, Object> validateAndCalculateDiscount(String code, Long orderAmount) {
        if (code == null || code.trim().isEmpty()) {
            throw new BadRequestException("Coupon code is required");
        }
        Coupon coupon = couponRepository.findByCouponCode(code.trim().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));

        if (!coupon.isActive()) {
            throw new BadRequestException("This coupon is no longer active");
        }

        if (coupon.getExpiry() != null && coupon.getExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This coupon has expired");
        }

        if (coupon.getMinimumOrder() != null && orderAmount < coupon.getMinimumOrder()) {
            throw new BadRequestException("Minimum order amount of Rs " + coupon.getMinimumOrder() + " required to use this coupon");
        }

        if (coupon.getCount() != null && coupon.getNumberOfToken() != null && coupon.getCount() >= coupon.getNumberOfToken()) {
            throw new BadRequestException("Coupon usage limit reached");
        }

        long discount = 0;
        if (coupon.getDiscountType() == Discount_Type.FLAT) {
            discount = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : 0;
        } else if (coupon.getDiscountType() == Discount_Type.PERCENTAGE) {
            long rawDiscount = (orderAmount * (coupon.getDiscountValue() != null ? coupon.getDiscountValue() : 10)) / 100;
            if (coupon.getUpto() != null && coupon.getUpto() > 0) {
                discount = Math.min(rawDiscount, coupon.getUpto());
            } else {
                discount = rawDiscount;
            }
        }

        discount = Math.min(discount, orderAmount);

        return Map.of(
                "valid", true,
                "couponCode", coupon.getCouponCode(),
                "discountAmount", discount,
                "finalAmount", (orderAmount - discount),
                "discountType", coupon.getDiscountType().name()
        );
    }
}
