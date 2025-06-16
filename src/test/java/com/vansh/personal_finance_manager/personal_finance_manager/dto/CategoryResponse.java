package com.vansh.personal_finance_manager.personal_finance_manager.dto;


import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;

public class CategoryResponse {
    private String name;
    private CategoryType type;
    private boolean isCustom;

    /** 
     * @return String
     */
    // Getters
    public String getName() {
        return name;
    }

    /** 
     * @return CategoryType
     */
    public CategoryType getType() {
        return type;
    }

    /** 
     * @return boolean
     */
    public boolean isCustom() {
        return isCustom;
    }
    /** 
     * @param name
     */
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * @param type
     */
    public void setType(CategoryType type) {
        this.type = type;
    }

    /** 
     * @param custom
     */
    public void setIsCustom(boolean custom) {
        isCustom = custom;
    }
}
