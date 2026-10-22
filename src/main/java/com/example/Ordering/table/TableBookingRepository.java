package com.example.Ordering.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {
    List<TableBooking> findByTable_IdAndBookingDateAndStatus(Long tableId, LocalDate bookingDate, String status);
    List<TableBooking> findByBookingDateAndStatus(LocalDate bookingDate, String status);
    List<TableBooking> findByUser_PhoneNumberOrderByBookingDateDesc(String phoneNumber);
    List<TableBooking> findByOrderByBookingDateDescStartTimeAsc();
}
