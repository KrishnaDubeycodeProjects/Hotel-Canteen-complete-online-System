package com.example.Ordering.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {
    private Long feedback_id;
    private String message;
    private Long star_by_user;
}
