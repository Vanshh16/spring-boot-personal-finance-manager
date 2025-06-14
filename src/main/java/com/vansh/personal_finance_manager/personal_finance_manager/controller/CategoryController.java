package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CategoryResponse;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateCategoryRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User getCurrentUser(HttpSession session) {
        Long id = (Long) session.getAttribute("userId");
        return userRepository.findById(id).orElseThrow();
    }
   
    // Get all categories
    @GetMapping
public ResponseEntity<?> getCategories(HttpSession session) {
    User user = getCurrentUser(session);
    if (user == null) {
        return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
    }
    
    List<Category> categories = categoryRepository.findByUserOrIsDefault(user, true);

    List<CategoryResponse> response = categories.stream().map(cat -> {
        CategoryResponse res = new CategoryResponse();
        res.setName(cat.getName());
        res.setType(cat.getType());
        res.setIsCustom(!cat.isDefault());
        return res;
    }).toList();

    return ResponseEntity.ok(Map.of("categories", response));
}

    // Create a custom category
        @PostMapping
public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request, HttpSession session) {
    String name = request.getName();
    if (name == null || name.isEmpty() || request.getType() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Category name is required"));
    }
    CategoryType type = request.getType();

    User user = getCurrentUser(session);

    if (categoryRepository.existsByNameAndUser(name, user)) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Category already exists"));
    }

    Category category = new Category(null, name, type, false, user);
    categoryRepository.save(category);

    CategoryResponse response = new CategoryResponse();
    response.setName(name);
    response.setType(type);
    response.setIsCustom(true);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}


    @DeleteMapping("/{name}")
public ResponseEntity<?> deleteCategory(@PathVariable String name, HttpSession session) {
    User user = getCurrentUser(session);

    Optional<Category> categoryOpt = categoryRepository.findByNameAndUser(name, user);
    if (categoryOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not found"));
    }

    Category category = categoryOpt.get();
    if (category.isDefault()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Cannot delete default category"));
    }

    categoryRepository.delete(category);
    return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
}

}

