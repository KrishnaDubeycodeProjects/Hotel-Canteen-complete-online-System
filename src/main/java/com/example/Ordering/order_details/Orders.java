package com.example.Ordering.order_details;

import com.example.Ordering.coupon.Coupon;
import com.example.Ordering.menuOrder.Order_Items;
import com.example.Ordering.payment.Payment;
import com.example.Ordering.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    private Long id;

    @Column(unique = true)
    private String tokenNumber;

    private String qrData;
    private Long totalAmount = 0L;
    private Long discountAmount = 0L;
    @Column(name = "payment_attempts", nullable = false)
    private int paymentAttempts = 0;

    @Column(name = "number_of_times_user_tried_payment", nullable = false)
    private int numberOfTimesUserTriedPayment = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order_Items> orderItems = new ArrayList<>();

    // The raw list of repeating menu IDs submitted by the client
    @ElementCollection
    @CollectionTable(name = "order_menu_ids", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "menu_id")
    private List<Integer> menuIds = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "order_status")
    private Order_Status order_status = Order_Status.Placed;

    private boolean payement_status = false;

    // Who is preparing the order
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cheff_id")
    private User cheff;

    // Who is serving / delivering the order
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiter_id")
    private User waiter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private boolean refund_status = false;
    private LocalDateTime refund_time;
    private String refund_reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_initiator_id")
    private User refund_Initiator;

    private LocalDateTime payment_time;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Boolean paid = false;

    // Type of delivery: DINE_IN, TAKEAWAY, HOME_DELIVERY, DOORSTEP
    private String deliveryType = "DINE_IN";
    private boolean delivery = false; // backward compatibility boolean
    private String deliveryAddress;
    private String tableNumber;

    private String otp_verify;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lastUpdateTime ASC")
    private List<Order_track> trackHistory = new ArrayList<>();
}
