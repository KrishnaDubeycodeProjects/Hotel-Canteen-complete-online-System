package com.example.Ordering.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Must be an interface — Spring Data JPA generates the implementation for you. */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(u.id), 0) FROM User u")
    Integer findMaxId();
}
