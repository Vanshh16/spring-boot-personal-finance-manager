package com.vansh.personal_finance_manager.personal_finance_manager.service;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.Category;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.CategoryType;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.CategoryRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
    }

    @Test
    public void testGetUserCategories() {
        Category c1 = new Category(null, "Salary", CategoryType.INCOME, true, user);
        Category c2 = new Category(null, "Food", CategoryType.EXPENSE, false, user);

        when(categoryRepository.findByUserOrIsDefault(user, true))
                .thenReturn(List.of(c1, c2));

        List<Category> categories = categoryService.getCategories(user);

        assertThat(categories).hasSize(2);
        verify(categoryRepository, times(1)).findByUserOrIsDefault(user, true);
    }
}
