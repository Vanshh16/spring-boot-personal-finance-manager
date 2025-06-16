package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);

        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    /** 
     * @param amount
     * @param date
     * @param categoryName
     * @param type
     * @return Transaction
     */
    // Helper to create transaction
    private Transaction createTransaction(BigDecimal amount, LocalDate date, String categoryName, CategoryType type) {
        Category category = new Category();
        category.setName(categoryName);
        category.setType(type);

        return new Transaction(1L, amount, date, "desc", category, category.getType(), mockUser);
    }

    // -------- MONTHLY REPORT --------
    @Test
    void testMonthlyReport_ReturnsCorrectAggregation() {
        int year = 2024, month = 5;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = List.of(
                createTransaction(BigDecimal.valueOf(1000), start.plusDays(1), "Salary", CategoryType.INCOME),
                createTransaction(BigDecimal.valueOf(200), start.plusDays(2), "Food", CategoryType.EXPENSE),
                createTransaction(BigDecimal.valueOf(50), start.plusDays(3), "Transport", CategoryType.EXPENSE)
        );

        when(transactionRepository.findByUserAndDateBetween(mockUser, start, end)).thenReturn(transactions);

        ResponseEntity<?> response = reportController.monthly(year, month, session);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(750, new BigDecimal(body.get("netSavings").toString()).intValue());
    }

    @Test
    void testMonthlyReport_EmptyTransactions() {
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = reportController.monthly(2024, 6, session);
        assertEquals(200, response.getStatusCodeValue());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("{}", body.get("totalIncome").toString());
        assertEquals("{}", body.get("totalExpenses").toString());
        assertEquals("0", body.get("netSavings").toString());
    }

    // -------- YEARLY REPORT --------
    @Test
    void testYearlyReport_ReturnsCorrectAggregation() {
        int year = 2024;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = List.of(
                createTransaction(BigDecimal.valueOf(2000), start.plusMonths(1), "Bonus", CategoryType.INCOME),
                createTransaction(BigDecimal.valueOf(500), start.plusMonths(2), "Rent", CategoryType.EXPENSE)
        );

        when(transactionRepository.findByUserAndDateBetween(mockUser, start, end)).thenReturn(transactions);

        ResponseEntity<?> response = reportController.yearly(year, session);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("1500", body.get("netSavings").toString());
    }

    @Test
    void testYearlyReport_EmptyTransactions() {
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = reportController.yearly(2024, session);
        assertEquals(200, response.getStatusCodeValue());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("0", body.get("netSavings").toString());
    }
}
