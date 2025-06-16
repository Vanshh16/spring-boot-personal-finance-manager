package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import java.math.BigDecimal;

public class UpdateGoalRequest {
    private BigDecimal targetAmount;
    private String targetDate;

    /** 
     * @return BigDecimal
     */
    // Getters and Setters
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    /** 
     * @param targetAmount
     */
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    /** 
     * @return String
     */
    public String getTargetDate() {
        return targetDate;
    }

    /** 
     * @param targetDate
     */
    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }
}
