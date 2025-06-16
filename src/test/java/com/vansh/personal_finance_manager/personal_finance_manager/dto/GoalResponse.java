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

    /** 
     * @return Long
     */
    // Getters and Setters
    public Long getId() {
        return id;
    }

    /** 
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** 
     * @return String
     */
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
     * @return LocalDate
     */
    public LocalDate getTargetDate() {
        return targetDate;
    }

    /** 
     * @param targetDate
     */
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    /** 
     * @return LocalDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** 
     * @param startDate
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** 
     * @return String
     */
    public String getCurrentProgress() {
        return currentProgress;
    }

    /** 
     * @param currentProgress
     */
    public void setCurrentProgress(String currentProgress) {
        this.currentProgress = currentProgress;
    }

    /** 
     * @return String
     */
    public String getProgressPercentage() {
        return progressPercentage;
    }

    /** 
     * @param progressPercentage
     */
    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    /** 
     * @return String
     */
    public String getRemainingAmount() {
        return remainingAmount;
    }

    /** 
     * @param remainingAmount
     */
    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
