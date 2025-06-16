package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateGoalRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.UpdateGoalRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.*;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.SavingsGoalRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SavingsGoalControllerTest {

    @InjectMocks
    private SavingsGoalController controller;

    @Mock private UserRepository userRepository;
    @Mock private SavingsGoalRepository goalRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private HttpSession session;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    /** 
     * @param amount
     * @param date
     * @param type
     * @return Transaction
     */
    private Transaction createTxn(BigDecimal amount, LocalDate date, CategoryType type) {
        Category category = new Category();
        category.setType(type);
        Transaction txn = new Transaction();
        txn.setAmount(amount);
        txn.setDate(date);
        txn.setCategory(category);
        txn.setUser(user);
        return txn;
    }

    // ---------- CREATE GOAL ----------

    @Test
    void testCreateGoal_Success() {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Buy a Bike");
        request.setTargetAmount(BigDecimal.valueOf(10000));
        request.setTargetDate(LocalDate.now().plusMonths(1).toString());

        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(LocalDate.parse(request.getTargetDate()));
        goal.setStartDate(LocalDate.now());
        goal.setUser(user);

        when(goalRepository.save(any())).thenReturn(goal);
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = controller.createGoal(request, session);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCreateGoal_TargetDateInPast() {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Vacation");
        request.setTargetAmount(BigDecimal.valueOf(5000));
        request.setTargetDate(LocalDate.now().minusDays(1).toString());

        ResponseEntity<?> response = controller.createGoal(request, session);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testCreateGoal_TargetAmountZero() {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Phone");
        request.setTargetAmount(BigDecimal.ZERO);
        request.setTargetDate(LocalDate.now().plusDays(10).toString());

        ResponseEntity<?> response = controller.createGoal(request, session);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testCreateGoal_NegativeAmount() {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Laptop");
        request.setTargetAmount(BigDecimal.valueOf(-1000));
        request.setTargetDate(LocalDate.now().plusDays(10).toString());

        ResponseEntity<?> response = controller.createGoal(request, session);
        assertEquals(400, response.getStatusCodeValue());
    }

    // ---------- GET GOALS ----------

    @Test
    void testGetGoals_Success() {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setGoalName("Save for Bike");
        goal.setTargetAmount(BigDecimal.valueOf(10000));
        goal.setTargetDate(LocalDate.now().plusMonths(1));
        goal.setStartDate(LocalDate.now());
        goal.setUser(user);

        when(goalRepository.findByUser(user)).thenReturn(List.of(goal));
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = controller.getGoals(session);
        assertEquals(200, response.getStatusCodeValue());
    }

    // ---------- GET SINGLE GOAL ----------

    @Test
    void testGetGoal_Success() {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setGoalName("Bike");
        goal.setTargetAmount(BigDecimal.valueOf(10000));
        goal.setTargetDate(LocalDate.now().plusMonths(1));
        goal.setStartDate(LocalDate.now());
        goal.setUser(user);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> response = controller.getGoal(1L, session);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetGoal_UnauthorizedAccess() {
        User otherUser = new User();
        otherUser.setId(2L);
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setUser(otherUser);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ResponseStatusException.class, () -> controller.getGoal(1L, session));
    }

    // ---------- UPDATE GOAL ----------

    @Test
    void testUpdateGoal_Success() {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setUser(user);
        goal.setTargetAmount(BigDecimal.valueOf(5000));
        goal.setStartDate(LocalDate.now());
        goal.setTargetDate(LocalDate.now().plusMonths(1));

        UpdateGoalRequest req = new UpdateGoalRequest();
        req.setTargetAmount(BigDecimal.valueOf(7000));
        req.setTargetDate(LocalDate.now().plusMonths(2).toString());

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(goal);
        when(transactionRepository.findByUserAndDateBetween(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<?> res = controller.updateGoal(1L, req, session);
        assertEquals(200, res.getStatusCodeValue());
    }

    // ---------- DELETE GOAL ----------

    @Test
    void testDeleteGoal_Success() {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setUser(user);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        ResponseEntity<?> res = controller.deleteGoal(1L, session);
        verify(goalRepository).delete(goal);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void testDeleteGoal_Forbidden() {
        SavingsGoal goal = new SavingsGoal();
        User otherUser = new User();
        otherUser.setId(2L);
        goal.setUser(otherUser);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ResponseStatusException.class, () -> controller.deleteGoal(1L, session));
    }
}
