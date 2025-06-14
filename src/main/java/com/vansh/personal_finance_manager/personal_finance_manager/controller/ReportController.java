package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

        @Autowired
        private TransactionRepository transactionRepository;

        @Autowired
        private UserRepository userRepository;

        private User getCurrentUser(HttpSession session) {
                Long id = (Long) session.getAttribute("userId");
                return userRepository.findById(id).orElseThrow();
        }

        @GetMapping("/monthly/{year}/{month}")
        public ResponseEntity<?> monthly(@PathVariable int year, @PathVariable int month, HttpSession session) {
                User user = getCurrentUser(session);
                LocalDate start = LocalDate.of(year, month, 1);
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

                List<Transaction> txns = transactionRepository.findByUserAndDateBetween(user, start, end);

                Map<String, BigDecimal> income = new HashMap<>();
                Map<String, BigDecimal> expense = new HashMap<>();
                BigDecimal incomeSum = BigDecimal.ZERO, expenseSum = BigDecimal.ZERO;

                for (Transaction t : txns) {
                        String name = t.getCategory().getName();
                        if (t.getCategory().getType() == CategoryType.INCOME) {
                                income.put(name, income.getOrDefault(name, BigDecimal.ZERO).add(t.getAmount()));
                                incomeSum = incomeSum.add(t.getAmount());
                        } else {
                                expense.put(name, expense.getOrDefault(name, BigDecimal.ZERO).add(t.getAmount()));
                                expenseSum = expenseSum.add(t.getAmount());
                        }
                }

                return ResponseEntity.ok(Map.of(
                                "month", month,
                                "year", year,
                                "totalIncome", income,
                                "totalExpenses", expense,
                                "netSavings", incomeSum.subtract(expenseSum)));
        }

        @GetMapping("/yearly/{year}")
        public ResponseEntity<?> yearly(@PathVariable int year, HttpSession session) {
                User user = getCurrentUser(session);
                LocalDate start = LocalDate.of(year, 1, 1);
                LocalDate end = LocalDate.of(year, 12, 31);

                List<Transaction> txns = transactionRepository.findByUserAndDateBetween(user, start, end);

                Map<String, BigDecimal> income = new HashMap<>();
                Map<String, BigDecimal> expense = new HashMap<>();
                BigDecimal incomeSum = BigDecimal.ZERO, expenseSum = BigDecimal.ZERO;

                for (Transaction t : txns) {
                        String name = t.getCategory().getName();
                        if (t.getCategory().getType() == CategoryType.INCOME) {
                                income.put(name, income.getOrDefault(name, BigDecimal.ZERO).add(t.getAmount()));
                                incomeSum = incomeSum.add(t.getAmount());
                        } else {
                                expense.put(name, expense.getOrDefault(name, BigDecimal.ZERO).add(t.getAmount()));
                                expenseSum = expenseSum.add(t.getAmount());
                        }
                }

                return ResponseEntity.ok(Map.of(
                                "year", year,
                                "totalIncome", income,
                                "totalExpenses", expense,
                                "netSavings", incomeSum.subtract(expenseSum)));
        }

}
