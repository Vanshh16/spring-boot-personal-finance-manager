package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import java.math.BigDecimal;

public class UpdateTransactionRequest {
    private BigDecimal amount;
    private String description;

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
