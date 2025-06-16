package com.vansh.personal_finance_manager.personal_finance_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple controller to indicate that the backend service is running.
 */
@RestController
@RequestMapping("/api")
public class HealthCheckController {

    /**
     * Health check endpoint.
     * 
     * @return A message indicating the service is up and running.
     */
    @GetMapping
    public String checkStatus() {
        return "Personal Finance Manager API is running.";
    }
}
