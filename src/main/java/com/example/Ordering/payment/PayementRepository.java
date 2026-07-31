package com.example.Ordering.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * FIX: your original declared "class PayementRepository extends JpaRepository<...>".
 * JpaRepository is an interface, so a class cannot extend it (this would not compile).
 * It must be an interface. Kept the existing (misspelled) name so callers don't break;
 * consider renaming to PaymentRepository later.
 */
@Repository
public interface PayementRepository extends JpaRepository<Payment, Long> {
}
