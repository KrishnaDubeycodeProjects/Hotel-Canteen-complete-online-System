package com.example.Ordering.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private Integer userId;
    private String phoneNumber;
    private String name;
    private String email;
    private Role role;
    private String message;
}
