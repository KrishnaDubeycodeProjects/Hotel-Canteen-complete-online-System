package com.example.Ordering.project_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsConditionRepository extends JpaRepository<Terms_Condition, Integer> {
}
