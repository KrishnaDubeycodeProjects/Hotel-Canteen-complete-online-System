package com.example.Ordering.table;

import com.example.Ordering.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "table_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Integer guestCount;
    private String specialRequest;
    private String status = "CONFIRMED"; // CONFIRMED, CANCELLED, COMPLETED

    private LocalDateTime createdAt = LocalDateTime.now();
}
