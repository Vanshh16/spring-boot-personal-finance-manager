package com.vansh.personal_finance_manager.personal_finance_manager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;

public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String category;
    private CategoryType type;

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
     * @return BigDecimal
     */
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
     * @return LocalDate
     */
    public LocalDate getDate() {
        return date;
    }

    /** 
     * @param date
     */
    public void setDate(LocalDate date) {
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

    /** 
     * @return CategoryType
     */
    public CategoryType getType() {
        return type;
    }

    /** 
     * @param type
     */
    public void setType(CategoryType type) {
        this.type = type;
    }
}
