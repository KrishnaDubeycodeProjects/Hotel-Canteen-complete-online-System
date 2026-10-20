package com.example.Ordering.order_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findBytokenNumber(String tokenNumber);
}
