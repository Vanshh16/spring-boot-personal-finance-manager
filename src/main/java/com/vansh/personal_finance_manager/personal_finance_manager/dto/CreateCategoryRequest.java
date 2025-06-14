package com.vansh.personal_finance_manager.personal_finance_manager.dto;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;

public class CreateCategoryRequest {
    private String name;
    private CategoryType type;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }
}
