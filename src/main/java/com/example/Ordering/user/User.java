package com.example.Ordering.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="app_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private Integer id;
    // OAUTH2
    private String email;
    private String name;
    private String password;
}
