package com.example.Ordering.order_details;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderDetailsController {

    private final OrderService orderService;

    public OrderDetailsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderDetailsResponseDTO> placeOrder(@Valid @RequestBody OrderDetailsRequestDTO request,
                                                              @AuthenticationPrincipal User user) {
        OrderDetailsResponseDTO response = orderService.placeOrder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<OrderDetailsResponseDTO> trackOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/track/{token}")
    public ResponseEntity<OrderDetailsResponseDTO> trackOrderByToken(@PathVariable String token) {
        return ResponseEntity.ok(orderService.getOrderByToken(token));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDetailsResponseDTO>> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrders(user.getPhoneNumber()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDetailsResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/kitchen")
    public ResponseEntity<List<OrderDetailsResponseDTO>> getKitchenOrders() {
        return ResponseEntity.ok(orderService.getKitchenOrders());
    }

    @PutMapping("/{id}/assign-chef")
    public ResponseEntity<OrderDetailsResponseDTO> assignChef(@PathVariable Long id,
                                                              @AuthenticationPrincipal User chef) {
        return ResponseEntity.ok(orderService.assignChef(id, chef));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailsResponseDTO> updateOrderStatus(@PathVariable Long id,
                                                                     @RequestBody Map<String, String> body,
                                                                     @AuthenticationPrincipal User user) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new BadRequestException("Status is required");
        }
        Order_Status status = null;
        for (Order_Status s : Order_Status.values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                status = s;
                break;
            }
        }
        if (status == null) {
            throw new BadRequestException("Invalid status: " + statusStr);
        }
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, user));
    }

    @GetMapping("/waiter")
    public ResponseEntity<List<OrderDetailsResponseDTO>> getWaiterOrders() {
        return ResponseEntity.ok(orderService.getWaiterOrders());
    }

    @PostMapping("/{id}/verify-otp")
    public ResponseEntity<OrderDetailsResponseDTO> verifyOtpAndDeliver(@PathVariable Long id,
                                                                       @RequestBody Map<String, String> body,
                                                                       @AuthenticationPrincipal User waiter) {
        String otp = body.get("otp");
        return ResponseEntity.ok(orderService.verifyOtpAndComplete(id, otp, waiter));
    }
}
