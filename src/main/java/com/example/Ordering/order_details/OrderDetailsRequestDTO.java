package com.example.Ordering.order_details;

import com.example.Ordering.payment.Payment_method;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsRequestDTO {
    @NotEmpty(message = "Menu IDs list cannot be empty")
    private List<Integer> menuIds;

    private String couponCode;
    private String deliveryType = "DINE_IN"; // DINE_IN, TAKEAWAY, HOME_DELIVERY, DOORSTEP
    private String deliveryAddress;
    private String tableNumber;
    private Payment_method paymentMethod = Payment_method.UPI;
    private Boolean autoPay = true; // Auto-trigger payment as requested: directly triggering API successfully done
}
