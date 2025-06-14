package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateGoalRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.GoalResponse;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.UpdateGoalRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.SavingsGoal;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.Transaction;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.SavingsGoalRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.TransactionRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SavingsGoalRepository goalRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private User getCurrentUser(HttpSession session) {
        Long id = (Long) session.getAttribute("userId");
        return userRepository.findById(id).orElseThrow();
    }

    private Map<String, Object> calculateGoalProgress(SavingsGoal goal, User user) {
    var transactions = transactionRepository.findByUserAndDateBetween(
            user, goal.getStartDate(), LocalDate.now());

    BigDecimal income = transactions.stream()
            .filter(t -> t.getCategory().getType() == CategoryType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal expense = transactions.stream()
            .filter(t -> t.getCategory().getType() == CategoryType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal saved = income.subtract(expense);
    BigDecimal remaining = goal.getTargetAmount().subtract(saved);

    BigDecimal percent = BigDecimal.ZERO;
    if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
        percent = saved.multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
    }

    DecimalFormat df = new DecimalFormat("0.0#");
    String formattedPercent = df.format(percent);

    return Map.of(
            "saved", saved,
            "percentage", formattedPercent,
            "remaining", remaining
    );
}


    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody CreateGoalRequest request, HttpSession session) {
        User user = getCurrentUser(session);
        SavingsGoal goal = new SavingsGoal();

        if (LocalDate.parse(request.getTargetDate()).isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Target date cannot be in the past."));
        }

        if (request.getStartDate() == null || request.getStartDate().isBlank()) {
            goal.setStartDate(LocalDate.now());
        } else {
            if (LocalDate.parse(request.getStartDate()).isAfter(LocalDate.parse(request.getTargetDate()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Start date cannot be after Target date."));
            }
            goal.setStartDate(LocalDate.parse(request.getStartDate()));
        }

        if (request.getTargetAmount().equals(BigDecimal.ZERO)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Target amount cannot be zero."));
        }
        if (request.getTargetAmount().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Target amount cannot be negative."));
        }

        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(LocalDate.parse(request.getTargetDate()));
        goal.setUser(user);

        SavingsGoal saved = goalRepository.save(goal);

        GoalResponse response = new GoalResponse();
         Map<String, Object> progress = calculateGoalProgress(goal, user);

        response.setId(saved.getId());
        response.setGoalName(saved.getGoalName());
        response.setTargetAmount(saved.getTargetAmount());
        response.setTargetDate(saved.getTargetDate());
        response.setStartDate(saved.getStartDate());
        response.setCurrentProgress(progress.get("saved").toString());
        response.setProgressPercentage(progress.get("percentage").toString());
        response.setRemainingAmount(progress.get("remaining").toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getGoals(HttpSession session) {
        User user = getCurrentUser(session);

        List<SavingsGoal> goals = goalRepository.findByUser(user);

        List<GoalResponse> response = goals.stream().map(goal -> {
            Map<String, Object> progress = calculateGoalProgress(goal, user);
            GoalResponse res = new GoalResponse();
            res.setId(goal.getId());
            res.setGoalName(goal.getGoalName());
            res.setTargetAmount(goal.getTargetAmount());
            res.setTargetDate(goal.getTargetDate());
            res.setStartDate(goal.getStartDate());
            res.setCurrentProgress(progress.get("saved").toString());
            res.setProgressPercentage(progress.get("percentage").toString());
            res.setRemainingAmount(progress.get("remaining").toString());
            return res;
        }).toList();

        return ResponseEntity.ok(Map.of("goals", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGoal(@PathVariable Long id, HttpSession session) {
        User user = getCurrentUser(session);
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!goal.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        GoalResponse response = new GoalResponse();
        Map<String, Object> progress = calculateGoalProgress(goal, user);

        response.setId(goal.getId());
        response.setGoalName(goal.getGoalName());
        response.setTargetAmount(goal.getTargetAmount());
        response.setTargetDate(goal.getTargetDate());
        response.setStartDate(goal.getStartDate());
        response.setCurrentProgress(progress.get("saved").toString());
        response.setProgressPercentage(progress.get("percentage").toString());
        response.setRemainingAmount(progress.get("remaining").toString());

        return ResponseEntity.ok(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable Long id, @RequestBody UpdateGoalRequest request,
            HttpSession session) {
        User user = getCurrentUser(session);
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!goal.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(LocalDate.parse(request.getTargetDate()));
        }

        SavingsGoal updated = goalRepository.save(goal);
        Map<String, Object> progress = calculateGoalProgress(updated, user);

        GoalResponse response = new GoalResponse();

        response.setId(updated.getId());
        response.setGoalName(updated.getGoalName());
        response.setTargetAmount(updated.getTargetAmount());
        response.setTargetDate(updated.getTargetDate());
        response.setStartDate(updated.getStartDate());
        response.setCurrentProgress(progress.get("saved").toString());
        response.setProgressPercentage(progress.get("percentage").toString());
        response.setRemainingAmount(progress.get("remaining").toString());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id, HttpSession session) {
        User user = getCurrentUser(session);
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!goal.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        goalRepository.delete(goal);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }
}
