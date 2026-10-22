package com.example.Ordering.feedback;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback_user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedback_id;
    private String userName;
    private String message;
    private Long star_by_user = 5L;
    private LocalDateTime createdAt = LocalDateTime.now();
}
