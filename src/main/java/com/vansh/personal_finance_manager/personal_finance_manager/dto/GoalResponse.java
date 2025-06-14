package com.vansh.personal_finance_manager.personal_finance_manager.dto;



import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalResponse {
    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private LocalDate startDate;
    private String currentProgress;
    private String progressPercentage;
    private String remainingAmount;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(String currentProgress) {
        this.currentProgress = currentProgress;
    }

    public String getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
