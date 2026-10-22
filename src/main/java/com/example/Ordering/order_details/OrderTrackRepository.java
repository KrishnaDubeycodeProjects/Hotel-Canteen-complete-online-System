package com.example.Ordering.order_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackRepository extends JpaRepository<Order_track, Long> {
    List<Order_track> findByOrder_IdOrderByLastUpdateTimeAsc(Long orderId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(t.id), 0) FROM Order_track t")
    Long findMaxId();
}
