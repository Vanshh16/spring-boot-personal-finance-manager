package com.vansh.personal_finance_manager.personal_finance_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    // @Email
    // private String email;

    @NotBlank
    private String username;
        
    @NotBlank
    private String password;
}
