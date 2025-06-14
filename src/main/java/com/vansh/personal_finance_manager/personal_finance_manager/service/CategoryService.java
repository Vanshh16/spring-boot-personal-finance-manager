package com.vansh.personal_finance_manager.personal_finance_manager.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void addDefaultCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    new Category(null, "Salary", CategoryType.INCOME, true, null),
                    new Category(null, "Food", CategoryType.EXPENSE, true, null),
                    new Category(null, "Rent", CategoryType.EXPENSE, true, null),
                    new Category(null, "Transportation", CategoryType.EXPENSE, true, null),
                    new Category(null, "Entertainment", CategoryType.EXPENSE, true, null),
                    new Category(null, "Healthcare", CategoryType.EXPENSE, true, null),
                    new Category(null, "Utilities", CategoryType.EXPENSE, true, null)
            ));
        }
    }

    public List<Category> getCategories(User user) {
        return categoryRepository.findByUserOrIsDefault(user, true);
    }

    public boolean isValidCategory(Long id, User user) {
        return categoryRepository.findByIdAndUserOrIsDefault(id, user, true).isPresent();
    }

    // public Category getCategoryById(Long id, User user) {
    //     return categoryRepository.findByIdAndUser(id, user)
    //             .orElseThrow(() -> new RuntimeException("Category not found"));
    // }
    public Category getCategoryByNameAndUserOrNameAndIsDefault(String name, User user) {
        return categoryRepository.findByNameAndUserOrNameAndIsDefault(name, user, name, true)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByNameAndIsDefault(categoryName, true);
    }

    public Optional<Category> getCategoryByNameandUser(String categoryName, User user) {
        return categoryRepository.findByNameAndUser(categoryName, user);
    }
}

