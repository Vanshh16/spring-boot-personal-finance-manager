package com.vansh.personal_finance_manager.personal_finance_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<com.vansh.personal_finance_manager.personal_finance_manager.entity.Category, Long> {
    List<Category> findByUserOrIsDefault(User user, boolean isDefault);
    Optional<Category> findByNameAndIsDefault(String name, boolean isDefault);
    Optional<Category> findByNameAndUser(String name, User user);
    Optional<Category> findByNameAndUserOrNameAndIsDefault(String name1, User user, String name2, boolean isDefault);
    Optional<Category> findByIdAndUserOrIsDefault(Long id, User user, boolean isDefault);
    boolean existsByNameAndUser(String name, User user);
    List<Category> findByTypeAndUserOrIsDefault(CategoryType type, User user, boolean isDefault);
}

