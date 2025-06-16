package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateTransactionRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.UpdateTransactionRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.service.CategoryService;
import com.vansh.personal_finance_manager.personal_finance_manager.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private HttpSession session;

    private User mockUser;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockCategory = new Category(1L, "Food", CategoryType.EXPENSE, false, mockUser);

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    // -------- POST --------
    @Test
    void testAddTransaction_Success() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setDate("2024-12-01");
        request.setCategory("Food");
        request.setDescription("Lunch");

        when(categoryRepository.findByNameAndUser("Food", mockUser)).thenReturn(Optional.of(mockCategory));
        Transaction mockTxn = new Transaction(1L, BigDecimal.valueOf(200), LocalDate.parse("2024-12-01"), "Lunch", mockCategory, mockCategory.getType(), mockUser);
        when(transactionService.addTransaction(any(), any(), any(), any(), any(), any())).thenReturn(mockTxn);

        ResponseEntity<?> response = transactionController.addTransaction(request, session);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testAddTransaction_ZeroAmount() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.ZERO);
        request.setDate("2024-12-01");
        request.setCategory("Food");

        ResponseEntity<?> response = transactionController.addTransaction(request, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddTransaction_CategoryNotFound() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setDate("2024-12-01");
        request.setCategory("Unknown");

        when(categoryRepository.findByNameAndUser("Unknown", mockUser)).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndIsDefault("Unknown", true)).thenReturn(Optional.empty());

        ResponseEntity<?> response = transactionController.addTransaction(request, session);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // -------- GET --------
    @Test
    void testGetTransactions_WithCategory() {
        Category cat = new Category(2L, "Food", CategoryType.EXPENSE, false, mockUser);
        Transaction txn = new Transaction(1L, BigDecimal.valueOf(100), LocalDate.now(), "Dinner", cat, cat.getType(),mockUser);

        when(categoryService.getCategoryByNameAndUserOrNameAndIsDefault("Food", mockUser)).thenReturn(cat);
        when(transactionService.getTransactionsByUserAndCategoryAndDateBetween(eq(mockUser), eq(cat), any(), any()))
                .thenReturn(List.of(txn));

        ResponseEntity<?> response = transactionController.getTransactions(null, null, "Food", session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("transactions"));
    }

    @Test
    void testGetTransactions_NoResults() {
        when(transactionService.getTransactionsByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = transactionController.getTransactions(null, null, null, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No transactions found", ((Map<?, ?>) response.getBody()).get("message"));
    }

    // -------- PUT --------
    @Test
    void testUpdateTransaction_Success() {
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(300));
        request.setDescription("Updated Description");

        Transaction txn = new Transaction(1L, BigDecimal.valueOf(200), LocalDate.now(), "Old", mockCategory, mockCategory.getType(), mockUser);

        when(transactionService.getTransactionById(1L, mockUser)).thenReturn(txn);

        ResponseEntity<?> response = transactionController.updateTransaction(1L, request, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTransaction_NotFound() {
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        when(transactionService.getTransactionById(2L, mockUser)).thenReturn(null);

        ResponseEntity<?> response = transactionController.updateTransaction(2L, request, session);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateTransaction_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(999L);
        Transaction txn = new Transaction(1L, BigDecimal.TEN, LocalDate.now(), "Test", mockCategory, mockCategory.getType(), otherUser);

        when(transactionService.getTransactionById(1L, mockUser)).thenReturn(txn);

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        ResponseEntity<?> response = transactionController.updateTransaction(1L, request, session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // -------- DELETE --------
    @Test
    void testDeleteTransaction_Success() {
        Transaction txn = new Transaction(1L, BigDecimal.valueOf(150), LocalDate.now(), "DeleteMe", mockCategory, mockCategory.getType(), mockUser);

        when(transactionService.getTransactionById(1L, mockUser)).thenReturn(txn);

        ResponseEntity<?> response = transactionController.deleteTransaction(1L, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction deleted successfully", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testDeleteTransaction_NotFound() {
        when(transactionService.getTransactionById(1L, mockUser)).thenReturn(null);

        ResponseEntity<?> response = transactionController.deleteTransaction(1L, session);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(5L);
        Transaction txn = new Transaction(1L, BigDecimal.TEN, LocalDate.now(), "OtherUserTxn", mockCategory, mockCategory.getType(),otherUser);

        when(transactionService.getTransactionById(1L, mockUser)).thenReturn(txn);

        ResponseEntity<?> response = transactionController.deleteTransaction(1L, session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
