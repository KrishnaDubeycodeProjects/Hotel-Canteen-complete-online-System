package com.example.Ordering.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Reset OTP/token is required")
    private String otp;

    @NotBlank(message = "New password is required")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String newPassword;
}
