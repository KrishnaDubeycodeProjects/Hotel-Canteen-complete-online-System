package com.example.Ordering.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT COALESCE(MAX(c.id), 0) FROM Category c")
    Long findMaxId();
}
