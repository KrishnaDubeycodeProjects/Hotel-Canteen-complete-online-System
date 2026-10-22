package com.example.Ordering.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data sent BY the client when creating/updating a User. No id, no password echo. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String phoneNumber;
    private String email;
    private String name;
    private String password;
    private Role role;
}
