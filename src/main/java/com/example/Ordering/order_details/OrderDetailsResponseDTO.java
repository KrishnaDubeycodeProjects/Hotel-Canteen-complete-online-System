package com.example.Ordering.order_details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsResponseDTO {
    private Long id;
    private String token_number;
    private String qr_data;
    private Long Total;
    private Long discountAmount;
    private int number_of_times_user_tried_payment;
    private LocalDateTime created_at;
    private Order_Status orderStatus;

    private String deliveryType;
    private String deliveryAddress;
    private String tableNumber;
    private Boolean paid;
    private String otp_verify;

    private String chefName;
    private String waiterName;
    private String couponCode;

    private boolean refund_status;
    private String refund_reason;

    private List<OrderItemDTO> items;
    private List<Integer> menuIds;
    private List<OrderTrackDTO> tracking;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDTO {
        private Integer menuId;
        private String dishName;
        private String dishImageUrl;
        private Integer quantity;
        private Integer price;
        private Long subTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderTrackDTO {
        private Order_Status status;
        private String updatedBy;
        private String remarks;
        private LocalDateTime timestamp;
    }
}
