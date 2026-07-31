package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsConditionResponseDTO {
    private Integer id;
    private String content;
    private Terms_Category terms_category;
}
