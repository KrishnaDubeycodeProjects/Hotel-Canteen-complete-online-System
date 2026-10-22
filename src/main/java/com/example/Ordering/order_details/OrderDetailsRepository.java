package com.example.Ordering.order_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByTokenNumber(String tokenNumber);
    Optional<Orders> findBytokenNumber(String tokenNumber);

    List<Orders> findByUser_PhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    List<Orders> findByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Orders o WHERE o.order_status = :status ORDER BY o.createdAt ASC")
    List<Orders> findByOrderStatus(@Param("status") Order_Status status);

    @Query("SELECT o FROM Orders o WHERE o.order_status IN (:s1, :s2) ORDER BY o.createdAt ASC")
    List<Orders> findActiveKitchenOrders(@Param("s1") Order_Status s1, @Param("s2") Order_Status s2);

    @Query("SELECT o FROM Orders o WHERE o.order_status = :s ORDER BY o.createdAt ASC")
    List<Orders> findReadyOrdersForWaiter(@Param("s") Order_Status s);

    @Query("SELECT COALESCE(MAX(o.id), 0) FROM Orders o")
    Long findMaxId();
}
