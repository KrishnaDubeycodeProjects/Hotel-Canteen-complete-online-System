package com.example.Ordering.feedback;

import com.example.Ordering.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping
    public ResponseEntity<List<Feedback_user>> getAllFeedback() {
        return ResponseEntity.ok(feedbackRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Feedback_user> submitFeedback(@RequestBody Feedback_user feedback,
                                                        @AuthenticationPrincipal User user) {
        if (user != null) {
            feedback.setUserName(user.getName());
        } else if (feedback.getUserName() == null || feedback.getUserName().trim().isEmpty()) {
            feedback.setUserName("Guest Customer");
        }
        feedback.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }
}
