package com.example.Ordering.payment;

import com.example.Ordering.order_details.OrderDetailsResponseDTO;
import com.example.Ordering.order_details.OrderService;
import com.example.Ordering.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;
    private final RefundRepository refundRepository;
    private final PayementRepository payementRepository;

    public PaymentController(OrderService orderService,
                             RefundRepository refundRepository,
                             PayementRepository payementRepository) {
        this.orderService = orderService;
        this.refundRepository = refundRepository;
        this.payementRepository = payementRepository;
    }

    @PostMapping("/pay")
    public ResponseEntity<OrderDetailsResponseDTO> payOrder(@RequestBody PaymentRequestDTO request,
                                                            @AuthenticationPrincipal User user) {
        OrderDetailsResponseDTO response = orderService.triggerPayment(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<OrderDetailsResponseDTO> refundOrder(@RequestBody RefundRequestDTO request,
                                                              @AuthenticationPrincipal User user) {
        OrderDetailsResponseDTO response = orderService.triggerRefund(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(payementRepository.findAll());
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<Refund>> getAllRefunds() {
        return ResponseEntity.ok(refundRepository.findAll());
    }
}
