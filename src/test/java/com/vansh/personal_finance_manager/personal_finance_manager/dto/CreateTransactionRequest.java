package com.vansh.personal_finance_manager.personal_finance_manager.dto;

import java.math.BigDecimal;

public class CreateTransactionRequest {
    private BigDecimal amount;
    private String date;
    private String description;
    private String category;

    /** 
     * @return BigDecimal
     */
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    /** 
     * @param amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** 
     * @return String
     */
    public String getDate() {
        return date;
    }

    /** 
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /** 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /** 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** 
     * @return String
     */
    public String getCategory() {
        return category;
    }

    /** 
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
