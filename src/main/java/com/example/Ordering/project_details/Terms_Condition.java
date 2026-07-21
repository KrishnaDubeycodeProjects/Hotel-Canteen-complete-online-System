package com.example.Ordering.project_details;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Terms_Condition {
    @Id
    private Integer id;
    private String content;
    private Terms_Category terms_category;
}
