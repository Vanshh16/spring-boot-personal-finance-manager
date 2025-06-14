package com.vansh.personal_finance_manager.personal_finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // Optional<User> findByEmail(String email);
}
