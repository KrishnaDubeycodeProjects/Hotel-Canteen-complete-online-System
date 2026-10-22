package com.example.Ordering.order_details;

import com.example.Ordering.coupon.Coupon;
import com.example.Ordering.coupon.CouponRepository;
import com.example.Ordering.coupon.CouponService;
import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import com.example.Ordering.menu.Menu;
import com.example.Ordering.menu.MenuRepository;
import com.example.Ordering.menuOrder.MenuOrderRepository;
import com.example.Ordering.menuOrder.Order_Items;
import com.example.Ordering.payment.*;
import com.example.Ordering.user.Role;
import com.example.Ordering.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderDetailsRepository orderRepository;
    private final MenuRepository menuRepository;
    private final MenuOrderRepository menuOrderRepository;
    private final OrderTrackRepository trackRepository;
    private final PayementRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;

    public OrderService(OrderDetailsRepository orderRepository,
                        MenuRepository menuRepository,
                        MenuOrderRepository menuOrderRepository,
                        OrderTrackRepository trackRepository,
                        PayementRepository paymentRepository,
                        RefundRepository refundRepository,
                        CouponRepository couponRepository,
                        CouponService couponService) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.menuOrderRepository = menuOrderRepository;
        this.trackRepository = trackRepository;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.couponRepository = couponRepository;
        this.couponService = couponService;
    }

    @Transactional
    public OrderDetailsResponseDTO placeOrder(OrderDetailsRequestDTO request, User user) {
        List<Integer> menuIds = request.getMenuIds();
        if (menuIds == null || menuIds.isEmpty()) {
            throw new BadRequestException("Order must contain at least one menu item");
        }

        // Count frequency of each menu ID (e.g., [1, 1, 2] -> ID 1: qty 2, ID 2: qty 1)
        Map<Integer, Integer> quantityMap = new LinkedHashMap<>();
        for (Integer id : menuIds) {
            quantityMap.put(id, quantityMap.getOrDefault(id, 0) + 1);
        }

        Orders order = new Orders();
        order.setId(orderRepository.findMaxId() + 1);
        order.setUser(user);
        order.setMenuIds(new ArrayList<>(menuIds));
        order.setDeliveryType(request.getDeliveryType() != null ? request.getDeliveryType().toUpperCase() : "DINE_IN");
        order.setDelivery("HOME_DELIVERY".equalsIgnoreCase(order.getDeliveryType()));
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTableNumber(request.getTableNumber());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrder_status(Order_Status.Placed);

        // Generate token number (e.g., TKN-1482)
        int randomToken = ThreadLocalRandom.current().nextInt(1000, 9999);
        order.setTokenNumber("TKN-" + randomToken);

        // Generate 4-digit OTP for counter / doorstep pickup
        int randomOtp = ThreadLocalRandom.current().nextInt(1000, 9999);
        order.setOtp_verify(String.valueOf(randomOtp));
        order.setQrData("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setPaymentAttempts(0);
        order.setNumberOfTimesUserTriedPayment(0);
        order.setPaid(false);
        order.setPayement_status(false);
        order.setRefund_status(false);

        long rawSubtotal = 0L;
        List<Order_Items> itemsList = new ArrayList<>();
        long currentItemMaxId = menuOrderRepository.findMaxId();

        for (Map.Entry<Integer, Integer> entry : quantityMap.entrySet()) {
            Integer menuId = entry.getKey();
            int qty = entry.getValue();

            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuId));

            if (!menu.isAvailable()) {
                throw new BadRequestException("Dish '" + menu.getName() + "' is currently unavailable");
            }
            if (menu.getQuantity() < qty) {
                throw new BadRequestException("Not enough stock for dish '" + menu.getName() + "'. Available: " + menu.getQuantity());
            }

            // Deduct inventory
            menu.setQuantity(menu.getQuantity() - qty);
            menuRepository.save(menu);

            Order_Items orderItem = new Order_Items();
            orderItem.setId(++currentItemMaxId);
            orderItem.setOrder(order);
            orderItem.setMenu(menu);
            orderItem.setDishName(menu.getName());
            orderItem.setDishImageUrl(menu.getImageUrl());
            orderItem.setDish_quantity(qty);
            orderItem.setPrice(menu.getPrice().intValue());
            long itemSubtotal = menu.getPrice() * qty;
            orderItem.setSubTotal(itemSubtotal);

            rawSubtotal += itemSubtotal;
            itemsList.add(orderItem);
        }

        order.setOrderItems(itemsList);

        // Coupon calculation
        long discount = 0L;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            try {
                Map<String, Object> couponRes = couponService.validateAndCalculateDiscount(request.getCouponCode(), rawSubtotal);
                discount = ((Number) couponRes.get("discountAmount")).longValue();
                Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode().trim().toUpperCase()).orElse(null);
                if (coupon != null) {
                    coupon.setCount(coupon.getCount() != null ? coupon.getCount() + 1 : 1);
                    couponRepository.save(coupon);
                    order.setCoupon(coupon);
                }
            } catch (Exception e) {
                // Ignore invalid coupon or log, proceed with 0 discount
            }
        }

        order.setDiscountAmount(discount);
        order.setTotalAmount(Math.max(0L, rawSubtotal - discount));

        // Direct payment trigger if requested
        if (Boolean.TRUE.equals(request.getAutoPay())) {
            Payment payment = new Payment();
            payment.setId(paymentRepository.findMaxId() + 1);
            payment.setTotal_amount(order.getTotalAmount());
            payment.setTransaction_id("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            payment.setPaid_at(LocalDateTime.now());
            payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : Payment_method.UPI);
            payment.setStatus("SUCCESS");

            order.setPayment(payment);
            order.setPaid(true);
            order.setPayement_status(true);
            order.setPayment_time(LocalDateTime.now());
            order.setOrder_status(Order_Status.Placed);
        }

        Orders savedOrder = orderRepository.save(order);

        // Add initial tracking entry
        addTrackEvent(savedOrder, Order_Status.Placed, "Customer (" + user.getName() + ")", "Order placed with " + itemsList.size() + " items");

        if (savedOrder.getPaid()) {
            addTrackEvent(savedOrder, Order_Status.Placed, "Payment Gateway", "Payment of Rs " + savedOrder.getTotalAmount() + " completed successfully via " + (savedOrder.getPayment() != null ? savedOrder.getPayment().getPaymentMethod() : "UPI"));
        }

        return mapToDto(savedOrder);
    }

    @Transactional
    public OrderDetailsResponseDTO triggerPayment(PaymentRequestDTO request, User user) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (Boolean.TRUE.equals(order.getPaid())) {
            throw new BadRequestException("Order is already paid");
        }

        Payment payment = new Payment();
        payment.setId(paymentRepository.findMaxId() + 1);
        payment.setTotal_amount(request.getTotal_amount() != null ? request.getTotal_amount() : order.getTotalAmount());
        payment.setTransaction_id("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaid_at(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : Payment_method.UPI);
        payment.setStatus("SUCCESS");

        order.setPayment(payment);
        order.setPaid(true);
        order.setPayement_status(true);
        order.setPayment_time(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Orders saved = orderRepository.save(order);
        addTrackEvent(saved, saved.getOrder_status(), "Payment Gateway", "Payment of Rs " + saved.getTotalAmount() + " received directly via " + payment.getPaymentMethod());

        return mapToDto(saved);
    }

    @Transactional
    public OrderDetailsResponseDTO triggerRefund(RefundRequestDTO request, User user) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (order.isRefund_status()) {
            throw new BadRequestException("Order has already been refunded");
        }

        Refund refund = new Refund();
        refund.setId(refundRepository.findMaxId() + 1);
        refund.setOrderId(order.getId());
        refund.setAmount(request.getAmount() != null ? request.getAmount() : order.getTotalAmount());
        refund.setReason(request.getReason() != null ? request.getReason() : "Customer requested cancellation");
        refund.setRefundStatus(Refund_status.ACCEPTED);
        refund.setRequested_at(LocalDateTime.now());
        refund.setProcessed_at(LocalDateTime.now());
        refund.setTransactionId("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        refundRepository.save(refund);

        order.setRefund_status(true);
        order.setRefund_time(LocalDateTime.now());
        order.setRefund_reason(refund.getReason());
        order.setRefund_Initiator(user);
        order.setOrder_status(Order_Status.Refunded);
        order.setUpdatedAt(LocalDateTime.now());

        // Restore dish quantities
        if (order.getOrderItems() != null) {
            for (Order_Items item : order.getOrderItems()) {
                if (item.getMenu() != null) {
                    Menu m = item.getMenu();
                    m.setQuantity(m.getQuantity() + item.getDish_quantity());
                    menuRepository.save(m);
                }
            }
        }

        Orders saved = orderRepository.save(order);
        addTrackEvent(saved, Order_Status.Refunded, user.getName() + " (" + user.getRole() + ")", "Direct refund of Rs " + refund.getAmount() + " processed successfully. Reason: " + refund.getReason());

        return mapToDto(saved);
    }

    @Transactional
    public OrderDetailsResponseDTO assignChef(Long orderId, User chef) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setCheff(chef);
        order.setOrder_status(Order_Status.Preparing);
        order.setUpdatedAt(LocalDateTime.now());

        Orders saved = orderRepository.save(order);
        addTrackEvent(saved, Order_Status.Preparing, "Chef " + chef.getName(), "Chef " + chef.getName() + " started preparing the order");

        return mapToDto(saved);
    }

    @Transactional
    public OrderDetailsResponseDTO updateOrderStatus(Long orderId, Order_Status status, User updater) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrder_status(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == Order_Status.Preparing && order.getCheff() == null && updater.getRole() == Role.CHEF) {
            order.setCheff(updater);
        }

        Orders saved = orderRepository.save(order);
        addTrackEvent(saved, status, updater.getName() + " (" + updater.getRole() + ")", "Order status changed to " + status);

        return mapToDto(saved);
    }

    @Transactional
    public OrderDetailsResponseDTO verifyOtpAndComplete(Long orderId, String otp, User waiter) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getOtp_verify() == null || !order.getOtp_verify().trim().equals(otp.trim())) {
            throw new BadRequestException("Invalid OTP verification code");
        }

        order.setWaiter(waiter);
        order.setOrder_status(Order_Status.Collected);
        order.setUpdatedAt(LocalDateTime.now());

        Orders saved = orderRepository.save(order);
        addTrackEvent(saved, Order_Status.Collected, "Waiter " + waiter.getName(), "Order verified with OTP and marked as collected/delivered");

        return mapToDto(saved);
    }

    public OrderDetailsResponseDTO getOrderById(Long id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToDto(order);
    }

    public OrderDetailsResponseDTO getOrderByToken(String token) {
        Orders order = orderRepository.findByTokenNumber(token)
                .or(() -> orderRepository.findBytokenNumber(token))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with token: " + token));
        return mapToDto(order);
    }

    public List<OrderDetailsResponseDTO> getUserOrders(String phoneNumber) {
        return orderRepository.findByUser_PhoneNumberOrderByCreatedAtDesc(phoneNumber).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDetailsResponseDTO> getAllOrders() {
        return orderRepository.findByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDetailsResponseDTO> getKitchenOrders() {
        return orderRepository.findActiveKitchenOrders(Order_Status.Placed, Order_Status.Preparing).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDetailsResponseDTO> getWaiterOrders() {
        return orderRepository.findReadyOrdersForWaiter(Order_Status.Ready).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void addTrackEvent(Orders order, Order_Status status, String updatedBy, String remarks) {
        Order_track track = new Order_track();
        track.setId(trackRepository.findMaxId() + 1);
        track.setOrder(order);
        track.setOrderStatus(status);
        track.setUpdatedBy(updatedBy);
        track.setRemarks(remarks);
        track.setLastUpdateTime(LocalDateTime.now());
        trackRepository.save(track);
    }

    public OrderDetailsResponseDTO mapToDto(Orders o) {
        List<OrderDetailsResponseDTO.OrderItemDTO> items = new ArrayList<>();
        if (o.getOrderItems() != null) {
            for (Order_Items item : o.getOrderItems()) {
                items.add(OrderDetailsResponseDTO.OrderItemDTO.builder()
                        .menuId(item.getMenu() != null ? item.getMenu().getMenu_id() : null)
                        .dishName(item.getDishName() != null ? item.getDishName() : (item.getMenu() != null ? item.getMenu().getName() : "Dish"))
                        .dishImageUrl(item.getDishImageUrl() != null ? item.getDishImageUrl() : (item.getMenu() != null ? item.getMenu().getImageUrl() : null))
                        .quantity(item.getDish_quantity())
                        .price(item.getPrice())
                        .subTotal(item.getSubTotal())
                        .build());
            }
        }

        List<OrderDetailsResponseDTO.OrderTrackDTO> tracks = new ArrayList<>();
        if (o.getTrackHistory() != null) {
            for (Order_track t : o.getTrackHistory()) {
                tracks.add(OrderDetailsResponseDTO.OrderTrackDTO.builder()
                        .status(t.getOrderStatus())
                        .updatedBy(t.getUpdatedBy())
                        .remarks(t.getRemarks())
                        .timestamp(t.getLastUpdateTime())
                        .build());
            }
        }

        return OrderDetailsResponseDTO.builder()
                .id(o.getId())
                .token_number(o.getTokenNumber())
                .qr_data(o.getQrData())
                .Total(o.getTotalAmount())
                .discountAmount(o.getDiscountAmount())
                .number_of_times_user_tried_payment(o.getPaymentAttempts())
                .created_at(o.getCreatedAt())
                .orderStatus(o.getOrder_status())
                .deliveryType(o.getDeliveryType())
                .deliveryAddress(o.getDeliveryAddress())
                .tableNumber(o.getTableNumber())
                .paid(o.getPaid())
                .otp_verify(o.getOtp_verify())
                .chefName(o.getCheff() != null ? o.getCheff().getName() : null)
                .waiterName(o.getWaiter() != null ? o.getWaiter().getName() : null)
                .couponCode(o.getCoupon() != null ? o.getCoupon().getCouponCode() : null)
                .refund_status(o.isRefund_status())
                .refund_reason(o.getRefund_reason())
                .items(items)
                .menuIds(o.getMenuIds())
                .tracking(tracks)
                .build();
    }
}
