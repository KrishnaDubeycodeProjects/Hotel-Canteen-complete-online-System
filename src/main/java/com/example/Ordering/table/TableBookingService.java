package com.example.Ordering.table;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import com.example.Ordering.user.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TableBookingService {

    private final RestaurantTableRepository tableRepository;
    private final TableBookingRepository bookingRepository;

    public TableBookingService(RestaurantTableRepository tableRepository,
                               TableBookingRepository bookingRepository) {
        this.tableRepository = tableRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findByActiveTrue();
    }

    public RestaurantTable createTable(RestaurantTable table) {
        return tableRepository.save(table);
    }

    public TableBooking bookTable(TableBookingRequestDTO request, User user) {
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        long durationMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        if (durationMinutes < 15 || durationMinutes > 120) {
            throw new BadRequestException("Table booking duration must be between 15 minutes and 2 hours (120 minutes). Requested: " + durationMinutes + " minutes.");
        }

        if (request.getBookingDate().isEqual(LocalDate.now()) && request.getStartTime().isBefore(LocalTime.now())) {
            throw new BadRequestException("Booking start time cannot be in the past");
        }

        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + request.getTableId()));

        if (!table.isActive()) {
            throw new BadRequestException("Selected table is currently unavailable");
        }

        if (request.getGuestCount() > table.getCapacity()) {
            throw new BadRequestException("Guest count (" + request.getGuestCount() + ") exceeds table capacity (" + table.getCapacity() + ")");
        }

        // Check for overlapping bookings
        List<TableBooking> existingBookings = bookingRepository.findByTable_IdAndBookingDateAndStatus(
                table.getId(), request.getBookingDate(), "CONFIRMED"
        );

        for (TableBooking existing : existingBookings) {
            boolean overlaps = request.getStartTime().isBefore(existing.getEndTime()) &&
                               request.getEndTime().isAfter(existing.getStartTime());
            if (overlaps) {
                throw new BadRequestException("Table " + table.getTableNumber() + " is already booked between " +
                        existing.getStartTime() + " and " + existing.getEndTime() + " on " + request.getBookingDate());
            }
        }

        TableBooking booking = new TableBooking();
        booking.setUser(user);
        booking.setTable(table);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setDurationMinutes((int) durationMinutes);
        booking.setGuestCount(request.getGuestCount());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public List<TableBooking> getUserBookings(String phoneNumber) {
        return bookingRepository.findByUser_PhoneNumberOrderByBookingDateDesc(phoneNumber);
    }

    public List<TableBooking> getAllBookings() {
        return bookingRepository.findByOrderByBookingDateDescStartTimeAsc();
    }

    public TableBooking cancelBooking(Long id, User user) {
        TableBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Check if user is owner or admin
        if (!booking.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}
