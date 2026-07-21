package com.example.Ordering.project_details;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.NaturalId;

@Entity
public class Privacy_policy {
    @Id
    private Long id;
    private String content;
    private Policy_Category policyCategory;
}
