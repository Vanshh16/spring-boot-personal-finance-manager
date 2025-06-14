package com.vansh.personal_finance_manager.personal_finance_manager.service;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction addTransaction(BigDecimal amount, LocalDate date, String description,
                                      Category category, User user, CategoryType type) {
        if (date.isAfter(LocalDate.now())) throw new RuntimeException("Date cannot be in the future");

        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setDate(date);
        t.setDescription(description);
        t.setCategory(category);
        t.setUser(user);
        t.setType(type);
        return transactionRepository.save(t);
    }

    public List<Transaction> getAllTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    public Transaction getTransactionById(Long id, User user) {
        return transactionRepository.findByIdAndUser(id, user);
    }

    public List<Transaction> getTransactionsByUserAndCategoryAndDateBetween(User user, Category category, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndCategoryAndDateBetween(user, category, start, end);
    }

    public List<Transaction> getTransactionsByUserAndDateBetween(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndDateBetween(user, start, end);
    }
    public void deleteTransaction(Long id, User user) {
        Transaction t = transactionRepository.findByIdAndUser(id, user);
        if (t == null) {
            throw new RuntimeException("Transaction not found");
        }
        if (!t.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized");
        transactionRepository.delete(t);
    }
}
