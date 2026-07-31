package com.example.Ordering.menuOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOrderRepository extends JpaRepository<Order_Items, Long> {
}
