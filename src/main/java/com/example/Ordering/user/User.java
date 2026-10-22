package com.example.Ordering.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="app_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String email;
    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private LocalDateTime createdAt = LocalDateTime.now();
}
