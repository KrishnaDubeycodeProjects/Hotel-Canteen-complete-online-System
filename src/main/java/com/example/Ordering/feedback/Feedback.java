package com.example.Ordering.feedback;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Feedback {
    @Id
    private Long id;
    private long cumulative_star;
    private long count_user;

}
