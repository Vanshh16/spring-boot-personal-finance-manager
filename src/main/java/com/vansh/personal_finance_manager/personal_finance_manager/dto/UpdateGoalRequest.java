package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import java.math.BigDecimal;

public class UpdateGoalRequest {
    private BigDecimal targetAmount;
    private String targetDate;

    // Getters and Setters
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }
}
