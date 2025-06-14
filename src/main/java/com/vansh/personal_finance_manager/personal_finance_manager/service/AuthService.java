package com.vansh.personal_finance_manager.personal_finance_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vansh.personal_finance_manager.personal_finance_manager.dto.LoginRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.dto.RegisterRequest;
import com.vansh.personal_finance_manager.personal_finance_manager.entity.User;
import com.vansh.personal_finance_manager.personal_finance_manager.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
        return user;
    }

    public User authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
