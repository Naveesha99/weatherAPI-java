package com.example.weatherAPI.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
