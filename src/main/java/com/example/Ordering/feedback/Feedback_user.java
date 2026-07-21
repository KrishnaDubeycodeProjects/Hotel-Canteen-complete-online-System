package com.example.Ordering.feedback;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Feedback_user {
    @Id
    private Long feedback_id;
    private String message;
    private Long star_by_user;
}
