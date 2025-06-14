package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;

public class CategoryResponse {
    private String name;
    private CategoryType type;
    private boolean isCustom;

    // Getters
    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public boolean isCustom() {
        return isCustom;
    }
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public void setIsCustom(boolean custom) {
        isCustom = custom;
    }
}
