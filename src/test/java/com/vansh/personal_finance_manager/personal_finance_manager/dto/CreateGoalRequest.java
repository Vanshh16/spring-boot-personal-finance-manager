package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import java.math.BigDecimal;

public class CreateGoalRequest {
    private String goalName;
    private BigDecimal targetAmount;
    private String targetDate;
    private String startDate;

    /** 
     * @return String
     */
    // Getters and Setters
    public String getGoalName() {
        return goalName;
    }

    /** 
     * @param goalName
     */
    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    /** 
     * @return BigDecimal
     */
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

    /** 
     * @return String
     */
    public String getStartDate() {
        return startDate;
    }

    /** 
     * @param startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
