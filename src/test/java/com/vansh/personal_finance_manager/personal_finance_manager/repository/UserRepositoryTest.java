package com.vansh.personal_finance_manager.personal_finance_manager.repository;

import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByUsername() {
        User user = new User(null, "test@example.com", "password", "Test User", "+1234567890");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }
}
