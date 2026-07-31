package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** NOTE: Policy_Category is currently an empty enum — add constants (e.g. DATA, COOKIES, GENERAL) before using this. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPolicyRequestDTO {
    private String content;
    private Policy_Category policyCategory;
}
