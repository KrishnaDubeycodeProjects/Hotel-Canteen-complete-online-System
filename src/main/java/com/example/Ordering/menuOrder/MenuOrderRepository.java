package com.example.Ordering.menuOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOrderRepository extends JpaRepository<Order_Items, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(i.id), 0) FROM Order_Items i")
    Long findMaxId();
}
