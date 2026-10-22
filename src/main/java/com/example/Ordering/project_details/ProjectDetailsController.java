package com.example.Ordering.project_details;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class ProjectDetailsController {

    private final ContactUsRepository contactUsRepository;
    private final PrivacyPolicyRepository privacyPolicyRepository;
    private final TermsConditionRepository termsConditionRepository;

    public ProjectDetailsController(ContactUsRepository contactUsRepository,
                                    PrivacyPolicyRepository privacyPolicyRepository,
                                    TermsConditionRepository termsConditionRepository) {
        this.contactUsRepository = contactUsRepository;
        this.privacyPolicyRepository = privacyPolicyRepository;
        this.termsConditionRepository = termsConditionRepository;
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, String>> submitContact(@RequestBody ContactUsRequestDTO request) {
        Contact_Us contact = new Contact_Us();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());
        contact.setSubmittedAt(LocalDateTime.now());
        contactUsRepository.save(contact);
        return ResponseEntity.ok(Map.of("message", "Thank you for contacting us! We will get back to you shortly."));
    }

    @GetMapping("/privacy")
    public ResponseEntity<List<Privacy_policy>> getPrivacyPolicies() {
        return ResponseEntity.ok(privacyPolicyRepository.findAll());
    }

    @GetMapping("/terms")
    public ResponseEntity<List<Terms_Condition>> getTermsAndConditions() {
        return ResponseEntity.ok(termsConditionRepository.findAll());
    }
}
