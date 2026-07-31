package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPolicyResponseDTO {
    private Long id;
    private String content;
    private Policy_Category policyCategory;
}
