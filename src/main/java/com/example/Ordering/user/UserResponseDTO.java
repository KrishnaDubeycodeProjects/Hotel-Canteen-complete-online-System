package com.example.Ordering.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data sent BACK to the client. Password is deliberately excluded. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer id;
    private String email;
    private String name;
}
