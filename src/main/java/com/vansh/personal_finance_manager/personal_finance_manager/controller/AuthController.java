package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.LoginRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.RegisterRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;
import com.vansh.personal_finance_manager.personal_finance_manager.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.getUsername();
        // String email = request.getEmail();

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already exists"));
        }

        // if (userRepository.findByEmail(email).isPresent()) {
        //     return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already exists"));
        // }

        try {
            User user = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully", "userId", user.getId()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "User registration failed"));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        String username = request.getUsername();
        String password = request.getPassword();
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        try {
            User user = authService.authenticate(request);
            session.setAttribute("userId", user.getId());
            return ResponseEntity.ok(Map.of("message", "Login successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
