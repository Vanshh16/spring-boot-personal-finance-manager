package com.vansh.personal_finance_manager.personal_finance_manager.service;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.LoginRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.RegisterRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- REGISTER TESTS ----------

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("vansh");
        request.setPassword("password123");
        request.setFullName("Vansh Nigam");
        request.setPhoneNumber("1234567890");

        when(userRepository.findByUsername("vansh")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");

        User savedUser = new User();
        savedUser.setUsername("vansh");
        savedUser.setPassword("encodedPass");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(request);

        assertEquals("vansh", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("vansh");

        when(userRepository.findByUsername("vansh")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Username already registered", ex.getMessage());
    }

    // ---------- AUTHENTICATE TESTS ----------

    @Test
    void testAuthenticate_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("vansh");
        request.setPassword("password123");

        User user = new User();
        user.setUsername("vansh");
        user.setPassword("encodedPass");

        when(userRepository.findByUsername("vansh")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);

        User result = authService.authenticate(request);

        assertEquals("vansh", result.getUsername());
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("password");

        when(userRepository.findByUsername("wrong")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("vansh");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("vansh");
        user.setPassword("encodedPass");

        when(userRepository.findByUsername("vansh")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPass")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Invalid credentials", ex.getMessage());
    }
}
