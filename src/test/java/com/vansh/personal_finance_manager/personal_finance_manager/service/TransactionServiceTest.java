package com.vansh.personal_finance_manager.personal_finance_manager.service;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);
        category.setUser(user);
    }

    @Test
    void testAddTransaction_Success() {
        Transaction tx = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        Transaction result = transactionService.addTransaction(
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Groceries",
                category,
                user,
                CategoryType.EXPENSE
        );

        assertThat(result).isNotNull();
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testAddTransaction_FutureDate_ThrowsException() {
        LocalDate futureDate = LocalDate.now().plusDays(2);

        assertThrows(RuntimeException.class, () ->
                transactionService.addTransaction(
                        new BigDecimal("150.00"),
                        futureDate,
                        "Invalid",
                        category,
                        user,
                        CategoryType.INCOME
                )
        );
    }

    @Test
    void testGetAllTransactions() {
        when(transactionRepository.findByUser(user)).thenReturn(List.of(new Transaction(), new Transaction()));

        List<Transaction> transactions = transactionService.getAllTransactions(user);

        assertThat(transactions).hasSize(2);
        verify(transactionRepository).findByUser(user);
    }

    @Test
    void testGetTransactionById() {
        Transaction tx = new Transaction();
        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(tx);

        Transaction found = transactionService.getTransactionById(1L, user);

        assertThat(found).isNotNull();
        verify(transactionRepository).findByIdAndUser(1L, user);
    }

    @Test
    void testDeleteTransaction_Success() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(user);

        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(tx);

        transactionService.deleteTransaction(1L, user);

        verify(transactionRepository).delete(tx);
    }

    @Test
    void testDeleteTransaction_NotFound() {
        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(1L, user));
    }

    @Test
    void testDeleteTransaction_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(999L);

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(otherUser);

        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(tx);

        // this test case will now always pass if you remove redundant unauthorized check in service
        // but if you keep it for extra safety, this test will catch such misuses
        assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(1L, user));
    }

    @Test
    void testGetTransactionsByUserAndCategoryAndDateBetween() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        when(transactionRepository.findByUserAndCategoryAndDateBetween(user, category, start, end))
                .thenReturn(List.of(new Transaction()));

        List<Transaction> result = transactionService.getTransactionsByUserAndCategoryAndDateBetween(user, category, start, end);

        assertThat(result).hasSize(1);
        verify(transactionRepository).findByUserAndCategoryAndDateBetween(user, category, start, end);
    }

    @Test
    void testGetTransactionsByUserAndDateBetween() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        when(transactionRepository.findByUserAndDateBetween(user, start, end))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        List<Transaction> result = transactionService.getTransactionsByUserAndDateBetween(user, start, end);

        assertThat(result).hasSize(2);
        verify(transactionRepository).findByUserAndDateBetween(user, start, end);
    }
}
