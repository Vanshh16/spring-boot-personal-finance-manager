package com.vansh.personal_finance_manager.personal_finance_manager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Only for APIs (safe if no forms)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/","/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(login -> login.disable()) // Disables default HTML login
        .httpBasic(basic -> basic.disable()) // No browser pop-up login
        .sessionManagement(session -> session
            .maximumSessions(1)
        );

    return http.build();
}


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

