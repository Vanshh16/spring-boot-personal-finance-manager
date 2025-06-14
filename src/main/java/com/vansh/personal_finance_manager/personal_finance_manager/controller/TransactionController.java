package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateTransactionRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.TransactionResponse;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.UpdateTransactionRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.service.CategoryService;
import com.vansh.personal_finance_manager.personal_finance_manager.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    private User getCurrentUser(HttpSession session) {
        Long id = (Long) session.getAttribute("userId");
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody CreateTransactionRequest request, HttpSession session) {

        User user = getCurrentUser(session);
        String name = request.getCategory();

        System.out.println(name);
        Optional<Category> categoryOpt = categoryRepository.findByNameAndUser(name, user);

        if (categoryOpt.isEmpty()) {
            categoryOpt = categoryRepository.findByNameAndIsDefault(name, true);
        }
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Category not found"));
        }
        Category category = categoryOpt.get();
        CategoryType type = category.getType();
        Transaction transaction = transactionService.addTransaction(request.getAmount(),
                LocalDate.parse(request.getDate()), request.getDescription(),
                category, user, type);

        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setDate(transaction.getDate());
        response.setCategory(category.getName());
        response.setDescription(transaction.getDescription());
        response.setType(type);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId,
            HttpSession session) {
        User user = getCurrentUser(session);

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : LocalDate.parse("0001-01-01");
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : LocalDate.parse("9999-12-31");

        List<Transaction> transactions;

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId, user);
            if (category != null) {
                System.out.println(category.getName());
            }
            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid category ID"));
            }
            transactions = transactionService.getTransactionsByUserAndCategoryAndDateBetween(user, category, start,
                    end);
        } else {
            transactions = transactionService.getTransactionsByUserAndDateBetween(user, start, end);
        }

        if (transactions.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No transactions found"));
        }
        List<TransactionResponse> txns = transactions.stream().map(txn -> {
            TransactionResponse response = new TransactionResponse();
            response.setId(txn.getId());
            response.setAmount(txn.getAmount());
            response.setDate(txn.getDate());
            response.setCategory(txn.getCategory().getName());
            response.setDescription(txn.getDescription());
            response.setType(txn.getCategory().getType());
            return response;
        }).toList();

        return ResponseEntity.ok(Map.of("transactions", txns));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody UpdateTransactionRequest request,
            HttpSession session) {
        User user = getCurrentUser(session);

        Transaction transaction = transactionService.getTransactionById(id, user);
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Transaction not found"));
        }

        if (!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not your transaction"));
        }

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        transactionRepository.save(transaction);

        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setDate(transaction.getDate());
        response.setCategory(transaction.getCategory().getName());
        response.setDescription(transaction.getDescription());
        response.setType(transaction.getCategory().getType());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, HttpSession session) {
        User user = getCurrentUser(session);

        Transaction transaction = transactionService.getTransactionById(id, user);

        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Transaction not found"));
        }

        if (!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not your transaction"));
        }

        transactionRepository.delete(transaction);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }

}
