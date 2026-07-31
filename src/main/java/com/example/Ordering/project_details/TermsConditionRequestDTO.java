package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** NOTE: Terms_Category is currently an empty enum — add constants before using this. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsConditionRequestDTO {
    private String content;
    private Terms_Category terms_category;
}
