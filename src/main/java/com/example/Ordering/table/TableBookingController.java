package com.example.Ordering.table;

import com.example.Ordering.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TableBookingController {

    private final TableBookingService bookingService;

    public TableBookingController(TableBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/api/tables")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(bookingService.getAllTables());
    }

    @PostMapping("/api/tables")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantTable> createTable(@RequestBody RestaurantTable table) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createTable(table));
    }

    @PostMapping("/api/table-bookings/book")
    public ResponseEntity<TableBooking> bookTable(@Valid @RequestBody TableBookingRequestDTO request,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookTable(request, user));
    }

    @GetMapping("/api/table-bookings/my-bookings")
    public ResponseEntity<List<TableBooking>> getMyBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getUserBookings(user.getPhoneNumber()));
    }

    @GetMapping("/api/table-bookings/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<List<TableBooking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/api/table-bookings/{id}/cancel")
    public ResponseEntity<TableBooking> cancelBooking(@PathVariable Long id,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, user));
    }

    @PutMapping("/api/table-bookings/{id}/complete")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<TableBooking> completeBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.completeBooking(id));
    }
}
