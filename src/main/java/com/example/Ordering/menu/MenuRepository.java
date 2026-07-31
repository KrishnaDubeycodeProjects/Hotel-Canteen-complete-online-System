package com.example.Ordering.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    List<Menu> findByQuantityGreaterThan(long quantity);

    List<Menu> findByDescriptionContainingIgnoreCase(String keyword);
}
