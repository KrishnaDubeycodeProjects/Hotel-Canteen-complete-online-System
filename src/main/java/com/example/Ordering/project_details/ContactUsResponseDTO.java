package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String subject;
    private String message;
}
