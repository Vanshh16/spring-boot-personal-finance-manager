package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.CategoryResponse;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.CreateCategoryRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private HttpSession session;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    // ------------------- GET -----------------------

    @Test
    void testGetCategories_Success() {
        Category category1 = new Category(1L, "Food", CategoryType.EXPENSE, true, null);
        Category category2 = new Category(2L, "CustomCategory", CategoryType.EXPENSE, false, mockUser);

        when(categoryRepository.findByUserOrIsDefault(mockUser, true)).thenReturn(List.of(category1, category2));

        ResponseEntity<?> response = categoryController.getCategories(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        List<?> categories = (List<?>) body.get("categories");
        assertEquals(2, categories.size());
    }

    // ------------------- POST -----------------------

    @Test
    void testCreateCategory_Success() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("SideBusinessIncome");
        request.setType(CategoryType.INCOME);

        when(categoryRepository.existsByNameAndUser("SideBusinessIncome", mockUser)).thenReturn(false);

        ResponseEntity<?> response = categoryController.createCategory(request, session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CategoryResponse res = (CategoryResponse) response.getBody();
        assertEquals("SideBusinessIncome", res.getName());
        assertEquals(CategoryType.INCOME, res.getType());
        assertTrue(res.isCustom());
    }

    @Test
    void testCreateCategory_Conflict() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Salary");
        request.setType(CategoryType.INCOME);

        when(categoryRepository.existsByNameAndUser("Salary", mockUser)).thenReturn(true);

        ResponseEntity<?> response = categoryController.createCategory(request, session);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Category already exists", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testCreateCategory_BadRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest(); // missing name and type

        ResponseEntity<?> response = categoryController.createCategory(request, session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category name is required", ((Map<?, ?>) response.getBody()).get("error"));
    }

    // ------------------- DELETE -----------------------

    @Test
    void testDeleteCategory_Success() {
        Category category = new Category(1L, "CustomCategory", CategoryType.EXPENSE, false, mockUser);
        when(categoryRepository.findByNameAndUser("CustomCategory", mockUser)).thenReturn(Optional.of(category));

        ResponseEntity<?> response = categoryController.deleteCategory("CustomCategory", session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.findByNameAndUser("NonExistent", mockUser)).thenReturn(Optional.empty());

        ResponseEntity<?> response = categoryController.deleteCategory("NonExistent", session);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testDeleteCategory_Forbidden_DefaultCategory() {
        Category defaultCat = new Category(1L, "Salary", CategoryType.INCOME, true, null);
        when(categoryRepository.findByNameAndUser("Salary", mockUser)).thenReturn(Optional.of(defaultCat));

        ResponseEntity<?> response = categoryController.deleteCategory("Salary", session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cannot delete default category", ((Map<?, ?>) response.getBody()).get("error"));
    }
}
