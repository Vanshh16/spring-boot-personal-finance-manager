package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.LoginRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.RegisterRequest;
// import com.vansh.personal_finance_manager.personal_finance_manager.dto.LoginRequest;
// import com.vansh.personal_finance_manager.personal_finance_manager.dto.RegisterRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- Register Tests ----------

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");
        request.setFullName("John Doe");
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.empty());
        when(authService.register(any())).thenReturn(user);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("User registered successfully", body.get("message"));
        assertEquals(1L, body.get("userId"));
    }

    @Test
    void testRegister_Conflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");
        request.setFullName("John Doe");
        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testRegister_Failure() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");
        request.setFullName("John Doe");
        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.empty());
        when(authService.register(any())).thenThrow(new RuntimeException());

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User registration failed", ((Map<?, ?>) response.getBody()).get("message"));
    }

    // ---------- Login Tests ----------

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        User user = new User();
        user.setId(1L);

        when(authService.authenticate(request)).thenReturn(user);

        ResponseEntity<?> response = authController.login(request, session);

        verify(session).setAttribute(eq("userId"), eq(user.getId()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@example.com");
        request.setPassword("wrongpassword");

        when(authService.authenticate(request)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = authController.login(request, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testLogin_MissingFields() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword(null);
        ResponseEntity<?> response = authController.login(request, session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username and password are required", ((Map<?, ?>) response.getBody()).get("error"));
    }

    // ---------- Logout Test ----------

    @Test
    void testLogout() {
        ResponseEntity<?> response = authController.logout(session);

        verify(session).invalidate();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", ((Map<?, ?>) response.getBody()).get("message"));
    }
}
