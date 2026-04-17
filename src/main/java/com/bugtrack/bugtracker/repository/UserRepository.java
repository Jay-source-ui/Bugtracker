package com.bugtrack.bugtracker.repository;

import com.bugtrack.bugtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // User-related methods only
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.role IN ('DEVELOPER', 'TESTER')")
    List<User> findAllDevelopersAndTesters();
}