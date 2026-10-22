package com.example.Ordering.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByRefundStatus(Refund_status status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(r.id), 0) FROM Refund r")
    Long findMaxId();
}
