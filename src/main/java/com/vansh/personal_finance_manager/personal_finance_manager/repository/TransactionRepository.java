package com.vansh.personal_finance_manager.personal_finance_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.Transaction;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    Transaction findByIdAndUser(Long id, User user);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUserAndCategoryAndDateBetween(User user, Category category, LocalDate start, LocalDate end);
}

